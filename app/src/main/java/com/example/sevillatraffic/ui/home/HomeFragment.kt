package com.example.sevillatraffic.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.MainActivity
import com.example.sevillatraffic.R
import com.example.sevillatraffic.R.layout
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.text.DateFormat
import java.util.*


class HomeFragment : AppCompatDialogFragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var txtLocationResult: TextView
    private lateinit var txtListLocation: TextView
    private lateinit var btnStartUpdates: Button
    private lateinit var btnStopUpdates: Button
    // location last updated time
    private var mLastUpdateTime: String? = null

    // bunch of location related apis
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    // boolean flag to toggle the ui
    private var mRequestingLocationUpdates: Boolean? = null

  //  private var listLocation = arrayListOf<Pair<Double,Double>>()
        //mutableMapOf<Double,Double>()

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // location updates interval - 10sec
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        // fastest updates interval - 5 sec
        // location updates will be received if another app is requesting the locations
        // than your app can handle
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
        private const val REQUEST_CHECK_SETTINGS = 100
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        txtLocationResult = root.findViewById(R.id.location_result)
        btnStartUpdates = root.findViewById(R.id.btn_start_location_updates)
        btnStopUpdates = root.findViewById(R.id.btn_stop_location_updates)
        txtListLocation = root.findViewById(R.id.listLocation)


        val button: Button = root.findViewById(R.id.button2)
        button.setOnClickListener{
            findNavController().navigate(R.id.nav_maps)
           /* val mapFrag = MapsFragment()
            val fragmentManager: FragmentManager? = fragmentManager
            val fragmentTransaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, mapFrag)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()*/
        }
          /*  val intent = Intent (context, MapsFragment::class.java)
            intent.putExtra("EXTRA_LOCATIONS",listLocation)
            requireActivity().startActivity(intent)*/
/*
            val nextFrag = MapsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_maps, nextFrag)
                .commit()
        }*/

        // initialize the necessary libraries
        init()

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        btnStartUpdates.setOnClickListener {
            startLocationButtonClick()
        }

        btnStopUpdates.setOnClickListener{
            stopLocationButtonClick()
        }
        return root
    }



    private fun init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        mSettingsClient = LocationServices.getSettingsClient(this.requireContext())
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                mCurrentLocation = locationResult.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
                updateLocationUI()
            }
        }
        mRequestingLocationUpdates = false
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval =
            UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval =
            FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    private fun restoreValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates")
            }
            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location")
            }
            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on")
            }
        }
        updateLocationUI()
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private fun updateLocationUI() {
        if (mCurrentLocation != null) {
 //           listLocation.add(Pair(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude))
            txtLocationResult.text = "Lat: " + mCurrentLocation!!.latitude + ", " +
                    "Lng: " + mCurrentLocation!!.longitude

            // giving a blink animation on TextView
            txtLocationResult.alpha = 0f
            txtLocationResult.animate().alpha(1f).duration = 300

        }
        toggleButtons()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates!!)
        outState.putParcelable("last_known_location", mCurrentLocation)
        outState.putString("last_updated_on", mLastUpdateTime)
    }

    private fun toggleButtons() {
        if (mRequestingLocationUpdates!!) {
            btnStartUpdates.isEnabled = false
            btnStopUpdates.isEnabled = true
        } else {
            btnStartUpdates.isEnabled = true
            btnStopUpdates.isEnabled = false
        }
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private fun startLocationUpdates() {
        mSettingsClient
            ?.checkLocationSettings(mLocationSettingsRequest)
            ?.addOnSuccessListener(this.requireActivity()) {
                Log.i(TAG, "All location settings are satisfied.")
                Toast.makeText(context, "Started location updates!", Toast.LENGTH_SHORT).show()
                if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
                mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper())
                updateLocationUI()
            }
            ?.addOnFailureListener(this.requireActivity()) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ")
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(this.activity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                        Toast.makeText(this.activity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
                updateLocationUI()
            }
    }

    private fun startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this.activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    mRequestingLocationUpdates = true
                    startLocationUpdates()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        // open device settings when the permission is
                        // denied permanently
                        openSettings()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun stopLocationButtonClick() {
        mRequestingLocationUpdates = false
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
            ?.removeLocationUpdates(mLocationCallback)
            ?.addOnCompleteListener(this.requireActivity()) {
                Toast.makeText(context, "Location updates stopped!", Toast.LENGTH_SHORT).show()
                toggleButtons()
            }
        //txtListLocation.text = listLocation.toString()
    }

    fun showLastKnownLocation() {
        if (mCurrentLocation != null) {
            Toast.makeText(context, "Lat: " + mCurrentLocation!!.latitude
                    + ", Lng: " + mCurrentLocation!!.longitude, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Last known location is not available!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.e(TAG, "User agreed to make required location settings changes.")
                Activity.RESULT_CANCELED -> {
                    Log.e(TAG, "User chose not to make required location settings changes.")
                    mRequestingLocationUpdates = false
                }
            }
        }
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package",
            BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates!! && checkPermissions()) {
            startLocationUpdates()
        }
        updateLocationUI()
    }

    private fun checkPermissions(): Boolean {
        val permissionState: Int = ActivityCompat.checkSelfPermission(this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        if (mRequestingLocationUpdates!!) {
            // pausing location updates
            stopLocationUpdates()
        }
    }

}


