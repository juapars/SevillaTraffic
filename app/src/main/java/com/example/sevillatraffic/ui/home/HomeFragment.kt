package com.example.sevillatraffic.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.R
import com.example.sevillatraffic.R.layout
import com.google.android.gms.maps.model.LatLng



class HomeFragment : AppCompatDialogFragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var btnManual: Button
    private lateinit var btnGoogle: Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(layout.fragment_home, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)
        btnManual = root.findViewById(R.id.btn_manual)
        btnGoogle = root.findViewById(R.id.btn_google)

        btnManual.setOnClickListener{
            findNavController().navigate(R.id.nav_manualRoute)
        }

        btnGoogle.setOnClickListener{
            findNavController().navigate(R.id.nav_googleRoute)
        }

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        return root
    }

}