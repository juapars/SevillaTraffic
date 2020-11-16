package com.example.sevillatraffic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sevillatraffic.R
import com.example.sevillatraffic.model.Traffic
import kotlinx.android.synthetic.main.row_traffic_layout.view.*

/*
    Adaptador para la vista con la lista de las notificaciones de tráfico de las rutas
     del usuario usando RecyclerView
 */
class ListTrafficAdapter(private val myDataset: List<Traffic>) : RecyclerView.Adapter<ListTrafficAdapter.MyViewHolder>() {

    lateinit var context : Context


    class MyViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        context = parent.context
        val rawView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_traffic_layout, parent, false)
        rawView.layoutParams = RecyclerView.LayoutParams(1000, 400)
        rawView.setPadding(20, 20, 0, 20)

        return MyViewHolder(rawView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        var direc = myDataset[position].direction?.replace(">","➤")

        holder.textView.txt_direction.text = direc
        holder.textView.txt_intensity.text = myDataset[position].intensity

        var detector = ContextCompat.getDrawable(context, R.drawable.ic_baseline_info_24)
        var operator = ContextCompat.getDrawable(context, R.drawable.ic_baseline_how_to_reg_24)


        if(myDataset[position].source?.contains("OPERADOR")!!){
            holder.textView.btn_traffic.background = operator
            holder.textView.btn_traffic.setOnClickListener {
                val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context,R.style.RightJustifyDialogWindowTitle)

                alertDialog.setTitle("Notificación de operador")
                alertDialog.setMessage("Este icono indica que esta notificación de tráfico ha sido confirmada por un operador," +
                        " es decir, que es fiable al 100% que existe tráfico en este tramo.")

                alertDialog.setNegativeButton("De acuerdo"){ dialog, which -> }
                alertDialog.show()
            }
        }else if(myDataset[position].source?.contains("DETECTORES")!!){
            holder.textView.btn_traffic.background = detector

            holder.textView.btn_traffic.setOnClickListener {
                val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context,R.style.RightJustifyDialogWindowTitle)

                alertDialog.setTitle("Notificación de detector")
                alertDialog.setMessage("Este icono indica que esta notificación de tráfico ha sido originada por un detector," +
                        " es decir, que al no estar confirmada por un operador, no podemos confirmar que exista realmente tráfico" +
                        " en este tramo, aunque por lo general es bastante probable.")

                alertDialog.setNegativeButton("De acuerdo"){ dialog, which -> }
                alertDialog.show()
            }

        }else{
            holder.textView.btn_traffic.visibility = View.GONE
        }
    }

    override fun getItemCount() = myDataset.size
}