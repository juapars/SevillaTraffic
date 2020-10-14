package com.example.sevillatraffic.ui.googleRoute

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.MainActivity
import com.example.sevillatraffic.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.BuildConfig
import kotlinx.android.synthetic.main.edit_route_fragment.*


class GoogleRouteFragment : Fragment() {

    private lateinit var googleRouteViewModel: GoogleRouteViewModel

    private lateinit var btnSearch: Button
    private lateinit var txtOrigin: EditText
    private lateinit var txtDestination: EditText
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

    private var listLocation = arrayListOf<LatLng>()
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
        googleRouteViewModel =
            ViewModelProviders.of(this).get(GoogleRouteViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_google_route, container, false)
        val textView: TextView = root.findViewById(R.id.text_googleRoute)
        btnSearch = root.findViewById(R.id.btn_search)

        txtOrigin = root.findViewById(R.id.lct_start)
        txtDestination = root.findViewById(R.id.lct_end)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        mSettingsClient = LocationServices.getSettingsClient(this.requireContext())
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // location is received
                mCurrentLocation = locationResult.lastLocation
            }
        }

        btnSearch.setOnClickListener{
            when {
                TextUtils.isEmpty(txtOrigin.text) -> {
                    Toast.makeText(
                        requireContext(),
                        "Ponle un nombre a tu ruta antes de guardar",
                        Toast.LENGTH_LONG
                    ).show()
                }
                TextUtils.isEmpty(txtDestination.text) -> {
                    Toast.makeText(
                        requireContext(),
                        "Añade una fecha de inicio",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    val datosAEnviar = Bundle()
                    datosAEnviar.putString("txtOrigin", txtOrigin.text.toString())
                    datosAEnviar.putString("txtDestination", txtDestination.text.toString())
                    findNavController().navigate(R.id.nav_maps, datosAEnviar)
                }
            }
        }


        googleRouteViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
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

    private fun checkPermissions(): Boolean {
        val permissionState: Int = ActivityCompat.checkSelfPermission(this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

}