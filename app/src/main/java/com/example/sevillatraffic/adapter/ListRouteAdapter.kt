package com.example.sevillatraffic.adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sevillatraffic.R
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import kotlinx.android.synthetic.main.row_layout.view.*

/*
    Adaptador para la vista con la lista de las rutas del usuario usando RecyclerView
 */

class ListRouteAdapter(val activity: Activity, private var myDataset: List<Route>, private var navController: NavController) : RecyclerView.Adapter<ListRouteAdapter.MyViewHolder>() {

    lateinit var context : Context
    private lateinit var nameR : String
    internal lateinit var db: DBHelper

    class MyViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        context = parent.context
        val rawView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false)
        rawView.layoutParams = RecyclerView.LayoutParams(1000, 500)
        rawView.setPadding(20, 20, 0, 20)

        db = DBHelper(activity)
        return MyViewHolder(rawView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
     //   holder.textView.txt_direction.text = myDataset[position].direction

        holder.textView.txt_name.text = myDataset[position].name.toString()
        holder.textView.txt_date.text = myDataset[position].date.toString()
        holder.textView.txt_origin.text = myDataset[position].origin.toString()
        holder.textView.txt_dest.text = myDataset[position].dest.toString() //lstTraffic[0].location.toString()
        holder.textView.txt_notifications.text = "De ${myDataset[position].notStart.toString()} a ${myDataset[position].notEnd.toString()}"
        holder.textView.btn_enable.isChecked = myDataset[position].enabled?.toBoolean()!!

        nameR = myDataset[position].name.toString()

        var remove: Button = holder.textView.findViewById(R.id.btn_remove)
        var edit: Button = holder.textView.findViewById(R.id.btn_edit)
        var enabled: Switch = holder.textView.findViewById(R.id.btn_enable)

        edit.setOnClickListener {
            var datos = Bundle()
            datos.putBoolean("edit",true)
            datos.putSerializable("route",myDataset[position])
            navController.navigate(R.id.nav_edit_route,datos)
        }

        remove.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)

            alertDialog.setTitle("Eliminar $nameR")
            alertDialog.setMessage("¿Deseas eliminar la ruta $nameR?")

            alertDialog.setPositiveButton("Si") {
                    dialog, which ->
                db.deleteRoute(myDataset[position].id.toString())
                this.notifyDataSetChanged()
                myDataset = db.allRoute
                this.notifyDataSetChanged()
                Toast.makeText(activity,"Ruta $nameR eliminada.", Toast.LENGTH_SHORT).show()
            }


            alertDialog.setNegativeButton("No"
            ) { dialog, which ->

            }

            alertDialog.show()
        }

        enabled.setOnCheckedChangeListener{_, isChecked ->
            myDataset[position].enabled = isChecked.toString()
            db.updateRoute(myDataset[position])
            if(isChecked)
                Toast.makeText(activity,"Ruta activada", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity,"Ruta desactivada", Toast.LENGTH_SHORT).show()
            this.notifyDataSetChanged()
        }

    }

    override fun getItemCount() = myDataset.size
}