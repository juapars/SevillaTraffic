package com.example.sevillatraffic.ui.editRoute

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.R
import com.example.sevillatraffic.adapter.ListRouteAdapter
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.ui.manualRoute.ManualRouteViewModel
import kotlinx.android.synthetic.main.edit_route_fragment.*
import kotlinx.android.synthetic.main.my_routes_fragment.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class EditRouteFragment : Fragment() {

    companion object {
        fun newInstance() = EditRouteFragment()
    }
    private var lstRoutes: List<Route> = ArrayList<Route>()
    private lateinit var viewModel: EditRouteViewModel
    internal lateinit var db: DBHelper


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(EditRouteViewModel::class.java)
        val root = inflater.inflate(R.layout.edit_route_fragment, container, false)

        db = DBHelper(requireContext())

        val textView: TextView = root.findViewById(R.id.txt_edit)

        viewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val btn_save: Button = root.findViewById(R.id.btn_save)

        btn_save.setOnClickListener {
            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val formatted = current.format(formatter)
            val route = Route((0..1000).random(), edt_name.text.toString(), formatted,
                "OWORIGIN","OWOEST", edt_notStart.text.toString(),edt_notEnd.text.toString())
            Log.d("GUARDAR","GUARDANDO RUTAAAAAAAAAAAAAAAAAAAAAAAAAAAAA $route")
            db.addRoute(route)
            findNavController().navigate(R.id.nav_my_routes)
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditRouteViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun refreshData(routes: ListView) {
        lstRoutes = db.allRoute
        val adapter = ListRouteAdapter(requireActivity(), lstRoutes)
        routes.adapter = adapter
    }


}