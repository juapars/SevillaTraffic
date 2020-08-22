package com.example.sevillatraffic.ui.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.R
import com.example.sevillatraffic.mapas.model.DirectionResponses
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import kotlin.collections.ArrayList

class MapsFragment : Fragment(), OnMapReadyCallback {


    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mMap: GoogleMap
    private var locatlist: ArrayList<LatLng>? = null
    private lateinit var origin : String
    private lateinit var dest : String

    private lateinit var btnConfirm: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapsViewModel =
            ViewModelProviders.of(this).get(MapsViewModel::class.java)
        val root = inflater.inflate(R.layout.maps_fragment, container, false)

  //      txt = root.findViewById(R.id.text_map)


        mapsViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        val manager = requireActivity().supportFragmentManager
        val transaction = manager.beginTransaction()
        val fragment = SupportMapFragment()
        transaction.add(R.id.maps_view, fragment)
        transaction.commit()

        fragment.getMapAsync(this)

        btnConfirm = root.findViewById(R.id.btn_confirm)

        btnConfirm.setOnClickListener {
            val alertDialog2: AlertDialog.Builder = AlertDialog.Builder(requireContext())

            alertDialog2.setTitle("Confirmar ruta")
            alertDialog2.setMessage("¿Es esta la ruta que quiere guardar?")

            alertDialog2.setPositiveButton("Si") {
                    dialog, which -> // Write your code here to execute after dialog
               /* Toast.makeText(requireContext(), "You clicked on YES", Toast.LENGTH_SHORT)
                    .show()*/
                val datosAEnviar = Bundle()
                datosAEnviar.putSerializable("origin", origin)
                datosAEnviar.putSerializable("dest", dest)
                datosAEnviar.putBoolean("edit", false)
                findNavController().navigate(R.id.nav_edit_route,datosAEnviar)
            }

            alertDialog2.setNegativeButton("No"
            ) { dialog, which -> // Write your code here to execute after dialog
                /*Toast.makeText(
                    requireContext(),"You clicked on NO", Toast.LENGTH_SHORT)
                    .show()
                dialog.cancel()*/
                findNavController().navigate(R.id.nav_newRoute)
            }

            alertDialog2.show()
        }

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 0
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
/*
        // Add a marker in Seville and move the camera
        val seville = LatLng(37.38, -5.98)
        var allPoints = mutableListOf<LatLng>()

        mMap.addMarker(MarkerOptions().position(seville).title("Marker in Seville"))

        var camPos = CameraPosition.builder().target(seville).zoom(16f).build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
        mMap.setOnMapClickListener {
            allPoints.add(it)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it))
        }*/

        if(arguments?.getSerializable("listLoc") !=null) {
            locatlist = arguments?.getSerializable("listLoc") as ArrayList<LatLng>
        }
        if (locatlist.isNullOrEmpty()){
            val apiServices = RetrofitClient.apiServices(this.requireContext())
            origin = requireArguments().getString("txtOrigin").toString()
            dest = requireArguments().getString("txtDestination").toString()
            if (origin != null && dest != null) {
                apiServices.getDirection(origin,dest,getString(R.string.api_key))
                    .enqueue(object : Callback<DirectionResponses> {
                        override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                            drawPolyline(response)

                            Log.d("MapsFragment", "OWOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO$response")
                            Log.d("bisa dong oke", response.message())
                        }

                        override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                            Log.e("anjir error", t.localizedMessage)
                        }
                    })
            }
            mMap.addMarker(MarkerOptions().position(getCoordinates(origin)))
            mMap.addMarker(MarkerOptions().position(getCoordinates(dest)))

        }else{
            Log.d("OWOOOOOOOOOOOOOOO","OWOOOOOOOOOOOOOOOOOOOOOOOOOOO${locatlist!![locatlist!!.size-1]}")

            origin = getAddress(locatlist!![0])
            dest = getAddress(locatlist!![locatlist!!.size-1])
/*
            val city: String = addresses[0].getLocality()
            val state: String = addresses[0].getAdminArea()
            val country: String = addresses[0].getCountryName()
            val postalCode: String = addresses[0].getPostalCode()
            val knownName: String = addresses[0].getFeatureName()*/

            var camPos = CameraPosition.builder().target(locatlist!![locatlist!!.size-1]).zoom(16f).build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
            var pl = PolylineOptions()
            for (a in locatlist!!) {
                pl.add(a)
            }
            var mOrigin = MarkerOptions().position(locatlist!![0])
            mMap.addMarker(MarkerOptions().position(locatlist!![0]))
            mMap.addMarker(MarkerOptions().position(locatlist!![locatlist!!.size-1]))
            mMap.addPolyline(pl.width(10f).color(Color.RED))
        }

        /*
         val fkip = LatLng(37.389280, -5.970090)
        val monas = LatLng(37.380736, -6.005469)
        val fromFKIP = fkip.latitude.toString() + "," + fkip.longitude.toString()
        val toMonas = monas.latitude.toString() + "," + monas.longitude.toString()
*/
    }

    private fun getAddress(coordinates: LatLng): String{
        var geocoder = Geocoder(requireContext(), Locale.getDefault())

        var res = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
        return res[0].getAddressLine(0)
    }
    private fun getCoordinates(name: String): LatLng{
        var geocoder = Geocoder(requireContext(), Locale.getDefault())

        var res = geocoder.getFromLocationName(name,1)
        return LatLng(res[0].latitude,res[0].longitude)
    }

    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        mMap.addPolyline(polyline)
        var camPos = CameraPosition.builder().target(polyline.points[0]).zoom(14f).build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("key") apiKey: String): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(context: Context): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.resources.getString(R.string.base_url))
                .build()

            return retrofit.create<ApiServices>(
                ApiServices::class.java)
        }
    }
}