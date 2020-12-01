package com.example.sevillatraffic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sevillatraffic.onboarding.IntroActivity

/*
    Actividad para mostrar la pantalla inicial o SplashScreen
 */

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish()

    }
}