package com.example.sevillatraffic.ui.myroutes

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sevillatraffic.R
import com.example.sevillatraffic.adapter.ListRouteAdapter
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route

class MyRoutesFragment : Fragment() {

    private var lstRoutes: List<Route> = ArrayList()
    private lateinit var viewModel: MyRoutesViewModel
    internal lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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
    }


    private fun refreshData(routes: RecyclerView) {
        lstRoutes = db.allRoute
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = ListRouteAdapter(requireActivity(),lstRoutes,findNavController())

        recyclerView = routes.apply {

            setHasFixedSize(true)

            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }


}