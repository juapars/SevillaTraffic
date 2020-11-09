package com.example.sevillatraffic.ui.googleRoute

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.karumi.dexter.BuildConfig


class GoogleRouteFragment : Fragment() {

    private lateinit var googleRouteViewModel: GoogleRouteViewModel

    private lateinit var btnSearch: Button
    private lateinit var txtOrigin: EditText
    private lateinit var txtDestination: EditText

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mCurrentLocation: Location? = null
    private var mRequestingLocationUpdates: Boolean? = null


    companion object {
        private val TAG = MainActivity::class.java.simpleName
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

                getConnectivityStatusString(requireContext()) == 0 -> {
                    Toast.makeText(
                        requireContext(),
                        "Necesitas tener conexión a Internet para realizar la búsqueda",
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
            Toast.makeText(
                context, "Lat: " + mCurrentLocation!!.latitude
                        + ", Lng: " + mCurrentLocation!!.longitude, Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(context, "Last known location is not available!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> Log.e(
                    TAG,
                    "User agreed to make required location settings changes."
                )
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
        val uri = Uri.fromParts(
            "package",
            BuildConfig.APPLICATION_ID, null
        )
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun getConnectivityStatusString(context: Context): Int? {
        Log.e("GOOGLE CONNECTIVITY","ENTRAMOS EN METODO")
        var status = 0
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        Log.e("GOOGLE CONNECTIVITY","VALOR ACTIVE NETWORK $activeNetwork")
        if (activeNetwork != null) {
            Log.e("GOOGLE CONNECTIVITY","VALOR ACTIVE TIPO ${activeNetwork.type} CUANDO WIFI ES ${ConnectivityManager.TYPE_WIFI} Y " +
                    " ${ConnectivityManager.TYPE_MOBILE}")
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = 1
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = 2
            }
        } else {
            status = 0
        }

        Log.e("GOOGLE CONNECTIVITY"," CUAL ES EL STATUS $status")
        return status
    }
    private fun checkPermissions(): Boolean {
        val permissionState: Int = ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

}