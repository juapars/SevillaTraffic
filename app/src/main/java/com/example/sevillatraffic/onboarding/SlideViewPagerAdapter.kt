package com.example.sevillatraffic.onboarding

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.sevillatraffic.MainActivity
import com.example.sevillatraffic.R


class SlideViewPagerAdapter(var ctx: Context, var viewPager: ViewPager) : PagerAdapter() {
    override fun getCount(): Int {
        return 5
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.slide_screen, container, false)

        val ind1 = view.findViewById<ImageView>(R.id.ind1)
        val ind2 = view.findViewById<ImageView>(R.id.ind2)
        val ind3 = view.findViewById<ImageView>(R.id.ind3)
        val ind4 = view.findViewById<ImageView>(R.id.ind4)
        val ind5 = view.findViewById<ImageView>(R.id.ind5)

        val btnGetStarted = view.findViewById<Button>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            val intent = Intent(ctx, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)
        }

       when (position) {
            0 -> {
                view.background = ctx.getDrawable(R.drawable.welcome_1)

                ind1.setImageResource(R.drawable.seleted)
                ind2.setImageResource(R.drawable.unselected)
                ind3.setImageResource(R.drawable.unselected)
                ind4.setImageResource(R.drawable.unselected)
                ind5.setImageResource(R.drawable.unselected)

            }
            1 -> {
                view.background = ctx.getDrawable(R.drawable.tut1_1)

                ind1.setImageResource(R.drawable.unselected)
                ind2.setImageResource(R.drawable.seleted)
                ind3.setImageResource(R.drawable.unselected)
                ind4.setImageResource(R.drawable.unselected)
                ind5.setImageResource(R.drawable.unselected)
            }
            2 -> {
                view.background = ctx.getDrawable(R.drawable.tut2_1)

                ind1.setImageResource(R.drawable.unselected)
                ind2.setImageResource(R.drawable.unselected)
                ind3.setImageResource(R.drawable.seleted)
                ind4.setImageResource(R.drawable.unselected)
                ind5.setImageResource(R.drawable.unselected)
            }
            3 -> {
                view.background = ctx.getDrawable(R.drawable.tut3_1)

                ind1.setImageResource(R.drawable.unselected)
                ind2.setImageResource(R.drawable.unselected)
                ind3.setImageResource(R.drawable.unselected)
                ind4.setImageResource(R.drawable.seleted)
                ind5.setImageResource(R.drawable.unselected)

         }

            4 -> {
                view.background = ctx.getDrawable(R.drawable.tut4_1)

                ind1.setImageResource(R.drawable.unselected)
                ind2.setImageResource(R.drawable.unselected)
                ind3.setImageResource(R.drawable.unselected)
                ind4.setImageResource(R.drawable.unselected)
                ind5.setImageResource(R.drawable.seleted)

                btnGetStarted.text = "Empezar"
         }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
