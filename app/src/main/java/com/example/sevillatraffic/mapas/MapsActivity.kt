package com.example.sevillatraffic.mapas

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sevillatraffic.R
import com.example.sevillatraffic.mapas.model.DirectionResponses
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var requestingLocationUpdates: Boolean = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mOrigin: LatLng
    private lateinit var mDestination: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mOrigin = LatLng(37.389280, -5.970090) //--------------------------------------------------------------------------------
        mDestination = LatLng(37.38259, -6.008911)//--------------------------------------------------------------------------------


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

   /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
    *
    * function initMap(): void {
   const map = new google.maps.Map(
   document.getElementById("map") as HTMLElement,
   {
   zoom: 3,
   center: { lat: 0, lng: -180 },
   mapTypeId: "terrain"
   }
   );

   const flightPlanCoordinates = [
   { lat: 37.772, lng: -122.214 },
   { lat: 21.291, lng: -157.821 },
   { lat: -18.142, lng: 178.431 },
   { lat: -27.467, lng: 153.027 }
   ];
   const flightPath = new google.maps.Polyline({
   path: flightPlanCoordinates,
   geodesic: true,
   strokeColor: "#FF0000",
   strokeOpacity: 1.0,
   strokeWeight: 2
   });

   flightPath.setMap(map);
   }
     */

    override fun onMapReady(googleMap: GoogleMap) {
       mMap = googleMap

       val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 0
       // Here, thisActivity is the current activity
       if (ContextCompat.checkSelfPermission(
               this,
               Manifest.permission.ACCESS_FINE_LOCATION
           )
           != PackageManager.PERMISSION_GRANTED
       ) {

           // Permission is not granted
           // Should we show an explanation?
           if (ActivityCompat.shouldShowRequestPermissionRationale(
                   this,
                   Manifest.permission.ACCESS_FINE_LOCATION
               )
           ) {
               // Show an explanation to the user *asynchronously* -- don't block
               // this thread waiting for the user's response! After the user
               // sees the explanation, try again to request the permission.
           } else {
               // No explanation needed, we can request the permission.
               ActivityCompat.requestPermissions(
                   this,
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
       //   mMap.setOnMyLocationButtonClickListener(this)
     //  mMap.setOnMyLocationClickListener(this);

       // Add a marker in Seville and move the camera
       val seville = LatLng(37.38, -5.98)
       var allPoints = mutableListOf<LatLng>()

       mMap.addMarker(MarkerOptions().position(seville).title("Marker in Seville"))
       mMap.moveCamera(CameraUpdateFactory.newLatLng(seville))
       mMap.setOnMapClickListener {
           allPoints.add(it)
           mMap.clear()
           mMap.addMarker(MarkerOptions().position(it))
       }

       var txt: TextView = findViewById(R.id.text_map)

       txt.text = intent.getSerializableExtra("EXTRA_LOCATIONS").toString()

       var locatlist: ArrayList<Pair<Double,Double>> = intent.getSerializableExtra("EXTRA_LOCATIONS") as ArrayList<Pair<Double, Double>>

       var pl = PolylineOptions()

       for (a in locatlist){
           pl.add(LatLng(a.first,a.second))
       }

       mMap.addPolyline(pl.width(10f).color(Color.RED))
/*
       mMap.addPolyline(
           PolylineOptions()
               .add(LatLng(37.389280, -5.970090))
               .add(LatLng(37.387818, -5.968076))
               .add(LatLng(37.387780, -5.967811))
               .add(LatLng(37.387540, -5.967787))
               .add(LatLng(37.387510, -5.959980))
               .width(10f)
               .color(Color.RED)

       )*/

       val fkip = LatLng(37.389280, -5.970090)
       val monas = LatLng(37.380736, -6.005469)
       val fromFKIP = fkip.latitude.toString() + "," + fkip.longitude.toString()
       val toMonas = monas.latitude.toString() + "," + monas.longitude.toString()

       val apiServices =
           RetrofitClient.apiServices(
               this
           )
       apiServices.getDirection(fromFKIP, toMonas, getString(R.string.api_key))
           .enqueue(object : Callback<DirectionResponses> {
               override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                   drawPolyline(response)
                   Log.d("bisa dong oke", response.message())
               }

               override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                   Log.e("anjir error", t.localizedMessage)
               }
           })
   }

    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        mMap.addPolyline(polyline)
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