package com.example.sevillatraffic.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.sevillatraffic.MainActivity
import com.example.sevillatraffic.R


class IntroActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var adapter: SlideViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        viewPager = findViewById(R.id.viewpager)
        adapter = SlideViewPagerAdapter(this,viewPager)

        viewPager.adapter = adapter
        if (isOpenAlread()) {
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            val editor = getSharedPreferences("slide", MODE_PRIVATE).edit()
            editor.putBoolean("slide", true)
            editor.commit()
        }
    }

    private fun isOpenAlread(): Boolean {
        val sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE)
        return sharedPreferences.getBoolean("slide", false)
    }


}