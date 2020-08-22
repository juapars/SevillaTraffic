package com.example.sevillatraffic.adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.sevillatraffic.R
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import kotlinx.android.synthetic.main.row_layout.view.*

class ListRouteAdapter(activity: Activity, var lstRoute: List<Route>, val parentFragment: Fragment): BaseAdapter() {

    private var inflater: LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var pos: Int = 0
    private var act = activity
    internal lateinit var db: DBHelper
    private lateinit var nameR : String

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView: View = inflater.inflate(R.layout.row_layout, null)

        pos = position

        rowView.txt_name.text = lstRoute[position].name.toString()
        rowView.txt_date.text = lstRoute[position].date.toString()
        rowView.txt_origin.text = lstRoute[position].origin.toString()
        rowView.txt_dest.text = lstRoute[position].dest.toString()
        rowView.txt_notifications.text = "De ${lstRoute[position].notStart.toString()} a ${lstRoute[position].notEnd.toString()}"

        nameR = lstRoute[position].name.toString()
        db = DBHelper(act)

        var remove: Button = rowView.findViewById(R.id.btn_remove)
        var edit: Button = rowView.findViewById(R.id.btn_edit)

        edit.setOnClickListener {
            var datos = Bundle()
            datos.putBoolean("edit",true)
            datos.putSerializable("route",lstRoute[position])
            parent!!.findNavController().navigate(R.id.nav_edit_route,datos)
        }

        remove.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(act)

            alertDialog.setTitle("Eliminar $nameR")
            alertDialog.setMessage("¿Deseas eliminar la ruta $nameR?")

            alertDialog.setPositiveButton("Si") {
                    dialog, which -> // Write your code here to execute after dialog
                /* Toast.makeText(requireContext(), "You clicked on YES", Toast.LENGTH_SHORT)
                     .show()*/
                db.deleteRoute(lstRoute[position].id.toString())
                this.notifyDataSetChanged()
                lstRoute = db.allRoute
                this.notifyDataSetChanged()
                Toast.makeText(act,"Ruta $nameR eliminada.",Toast.LENGTH_SHORT)
            }

            alertDialog.setNegativeButton("No"
            ) { dialog, which -> // Write your code here to execute after dialog
                /*Toast.makeText(
                    requireContext(),"You clicked on NO", Toast.LENGTH_SHORT)
                    .show()
                dialog.cancel()*/
            }

            alertDialog.show()
        }

        this.notifyDataSetChanged()
        return rowView
    }

    override fun getItem(position: Int): Any {
        return lstRoute[position]
    }

    override fun getItemId(position: Int): Long {
        return lstRoute[position].id.toLong()
    }

    override fun getCount(): Int {
        return lstRoute.size
    }

    fun getPosition(): Int{
        return pos
    }
}