package com.example.sevillatraffic.ui.editRoute

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.R
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import com.google.maps.android.PolyUtil.isLocationOnEdge
import kotlinx.android.synthetic.main.edit_route_fragment.*
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class EditRouteFragment : Fragment() {

    companion object {
        fun newInstance() = EditRouteFragment()
    }

    private lateinit var viewModel: EditRouteViewModel
    private lateinit var db: DBHelper
    private val CERO = "0"
    private val DOS_PUNTOS = ":"
    private val c = Calendar.getInstance()
    private val hour: Int = c.get(Calendar.HOUR_OF_DAY)
    private val minute: Int = c.get(Calendar.MINUTE)

    private lateinit var notStart: EditText
    private lateinit var ibStart: ImageButton
    private lateinit var notEnd: EditText
    private lateinit var ibEnd: ImageButton
    private var edit: Boolean = false


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

        notStart = root.findViewById(R.id.edt_notStart)
        notEnd = root.findViewById(R.id.edt_notEnd)
        ibStart = root.findViewById(R.id.ib_notStart)
        ibEnd = root.findViewById(R.id.ib_notEnd)
        edit = requireArguments().getBoolean("edit")

        if(edit){
            var name: EditText = root.findViewById(R.id.edt_name)
            var route = requireArguments().getSerializable("route") as Route
            name.setText(route.name)
            notStart.setText(route.notStart)
            notEnd.setText(route.notEnd)
        }

        ibStart.setOnClickListener {
            getTime(notStart)
        }
        ibEnd.setOnClickListener {
            getTime(notEnd)
        }

        btn_save.setOnClickListener {

            when {
                TextUtils.isEmpty(edt_name.text) -> {
                    Toast.makeText(
                        requireContext(),
                        "Ponle un nombre a tu ruta antes de guardar",
                        Toast.LENGTH_LONG
                    ).show()
                }
                TextUtils.isEmpty(edt_notStart.text) -> {
                    Toast.makeText(
                        requireContext(),
                        "Añade una fecha de inicio",
                        Toast.LENGTH_LONG
                    ).show()
                }
                TextUtils.isEmpty(edt_notEnd.text) -> {
                    Toast.makeText(
                        requireContext(),
                        "Añade una fecha fin",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    if (edit) {
                        var routeOld = requireArguments().getSerializable("route") as Route
                        val route = Route(
                            routeOld.id, edt_name.text.toString(), routeOld.date.toString(),
                            routeOld.origin.toString(),
                            routeOld.dest.toString(),
                            edt_notStart.text.toString(), edt_notEnd.text.toString(),
                            routeOld.placemarks.toString(),
                            routeOld.enabled.toString()
                        )
                        db.updateRoute(route)
                    } else {
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        val formatted = current.format(formatter)
                        val route = Route(
                            (0..1000).random(), edt_name.text.toString(), formatted,
                            "Desde " + requireArguments().getString("origin").toString(),
                            "A " + requireArguments().getString("dest").toString(),
                            edt_notStart.text.toString(), edt_notEnd.text.toString(),
                            "" + requireArguments().getString("placemarks"),
                            "True"
                        )

                        db.addRoute(route)

                    }
                    findNavController().navigate(R.id.nav_my_routes)
                }
            }
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditRouteViewModel::class.java)
    }

    private fun getTime(edt: EditText) {
        val pickHour = TimePickerDialog(
            requireContext(),
            { view, hourOfDay, minute ->
                val hourFormatter = if (hourOfDay < 10) java.lang.String.valueOf(CERO + hourOfDay) else hourOfDay.toString()
                val minuteFormatter = if (minute < 10) java.lang.String.valueOf(CERO + minute) else minute.toString()

                //Muestro la hora con el formato deseado
                edt.setText("$hourFormatter$DOS_PUNTOS$minuteFormatter")
            },
            hour, minute, true
        )
        pickHour.show()
    }

}