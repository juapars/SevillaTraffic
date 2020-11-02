package com.example.sevillatraffic.ui.options

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.MainActivity
import com.example.sevillatraffic.R
import com.example.sevillatraffic.adapter.GlobalClass

class OptionsFragment : Fragment() {

    companion object {
        fun newInstance() = OptionsFragment()
    }

    private lateinit var viewModel: OptionsViewModel
    private lateinit var detector: Switch
    private lateinit var fluid: Switch
    private lateinit var voice: Switch
    private lateinit var btnInfo: Button
    private lateinit var txtFluid: TextView

    private lateinit var global : GlobalClass

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root =  inflater.inflate(R.layout.options_fragment, container, false)

        global = activity?.application as GlobalClass

        btnInfo = root.findViewById(R.id.btn_app_info)
        txtFluid = root.findViewById(R.id.txt_opt_fluid)

        btnInfo.setOnClickListener {
            findNavController().navigate(R.id.nav_app_info)
        }

        detector = root.findViewById(R.id.swt_detector)
        fluid = root.findViewById(R.id.swt_fluid)
        voice = root.findViewById(R.id.swt_voice)

        detector.isChecked = global.get_enableDetectors()
        fluid.isChecked = global.get_enableFluid()
        voice.isChecked = global.get_enableVoice()

        detector.setOnCheckedChangeListener{_, isChecked ->
            global.set_enableDetectors(isChecked)
        }

        fluid.setOnCheckedChangeListener{_, isChecked ->
            global.set_enableFluid(isChecked)
        }

        voice.setOnCheckedChangeListener{_, isChecked ->
            global.set_enableVoice(isChecked)
        }


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OptionsViewModel::class.java)


    }
}