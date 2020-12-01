package com.example.sevillatraffic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sevillatraffic.adapter.DownloadXmlTask
import com.example.sevillatraffic.db.DBHelper
import com.google.android.material.navigation.NavigationView

/*
    Actividad principal de la aplicación, consistente en el menú lateral

 */

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)                  // Se establece la relación con la vista


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        db = DBHelper(this)                             // Se inicia la base de datos


//      Se comprueba si en la base de datos existen los datos del tráfico. De no ser así, se procede
//        a descargar el archivo y establecerlos en la base de datos
        if(db.allTraffic.isEmpty())  DownloadXmlTask(true,db, this).execute("http://trafico.sevilla.org/estado-trafico-CGM.kml")


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

// Se configuran las secciones del menú lateral, es decir, los enlaces a las vistas

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_notifications,
                R.id.nav_newRoute,
                R.id.nav_my_routes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        createNotificationChannel()

        checkPermission()
    }


    // Método para la creación del canal de notificaciones, para poder publicar las alertas del
    // tráfico que se generen

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1600", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // Método para controlar el menú de opciones en la barra de estado
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        val navController = findNavController(R.id.nav_host_fragment)
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.nav_options)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

}