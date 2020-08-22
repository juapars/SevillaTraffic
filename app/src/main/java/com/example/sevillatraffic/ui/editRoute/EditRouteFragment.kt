package com.example.sevillatraffic.ui.editRoute

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.R
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import kotlinx.android.synthetic.main.edit_route_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class EditRouteFragment : Fragment() {

    companion object {
        fun newInstance() = EditRouteFragment()
    }
    private var lstRoutes: List<Route> = ArrayList<Route>()
    private lateinit var viewModel: EditRouteViewModel
    internal lateinit var db: DBHelper
    private val CERO = "0"
    private val DOS_PUNTOS = ":"
    private val c = Calendar.getInstance()
    private val hora: Int = c.get(Calendar.HOUR_OF_DAY)
    private val minuto: Int = c.get(Calendar.MINUTE)

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
            obtenerHora(notStart)
        }
        ibEnd.setOnClickListener {
            obtenerHora(notEnd)
        }

        btn_save.setOnClickListener {
            if(edit){
                var routeOld = requireArguments().getSerializable("route") as Route
                val route = Route(
                    routeOld.id, edt_name.text.toString(), routeOld.date.toString(),
                    "Desde " + routeOld.origin.toString(),
                    "A " + routeOld.dest.toString(),
                    edt_notStart.text.toString(), edt_notEnd.text.toString()
                )
                db.updateRoute(route)
            }else {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                val formatted = current.format(formatter)
                val route = Route(
                    (0..1000).random(), edt_name.text.toString(), formatted,
                    "Desde " + requireArguments().getString("origin").toString(),
                    "A " + requireArguments().getString("dest").toString(),
                    edt_notStart.text.toString(), edt_notEnd.text.toString()
                )
                Log.d("GUARDAR", "GUARDANDO RUTAAAAAAAAAAAAAAAAAAAAAAAAAAAAA $route")
                db.addRoute(route)

            }
            findNavController().navigate(R.id.nav_my_routes)
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditRouteViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun obtenerHora(edt: EditText) {
        val recogerHora = TimePickerDialog(
            requireContext(),
            OnTimeSetListener { view, hourOfDay, minute -> //Formateo el hora obtenido: antepone el 0 si son menores de 10
                val horaFormateada =
                    if (hourOfDay < 10) java.lang.String.valueOf(CERO + hourOfDay) else hourOfDay.toString()
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                val minutoFormateado =
                    if (minute < 10) java.lang.String.valueOf(CERO + minute) else minute.toString()

                //Muestro la hora con el formato deseado
                edt.setText(horaFormateada + DOS_PUNTOS.toString() + minutoFormateado)
            }, //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
            hora, minuto, false
        )
        recogerHora.show()
    }

}