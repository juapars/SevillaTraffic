package com.example.sevillatraffic.ui.myroutes

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import com.example.sevillatraffic.R
import com.example.sevillatraffic.adapter.ListRouteAdapter
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic

class MyRoutesFragment : Fragment() {

    private var lstRoutes: List<Route> = ArrayList<Route>()
    private var lstTraffic: List<Traffic> = ArrayList()
    private lateinit var viewModel: MyRoutesViewModel
    internal lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(MyRoutesViewModel::class.java)
        val root = inflater.inflate(R.layout.my_routes_fragment, container, false)

        db = DBHelper(requireContext())

        refreshData(root.findViewById(R.id.lst_routes))


        viewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyRoutesViewModel::class.java)
        // TODO: Use the ViewModel
    }


    private fun refreshData(routes: ListView) {
        lstRoutes = db.allRoute
        val adapter = ListRouteAdapter(requireActivity(), lstRoutes, this)
        adapter.notifyDataSetChanged()
        routes.adapter = adapter
    }


}