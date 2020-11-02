package com.example.sevillatraffic.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.sevillatraffic.MainActivity
import com.example.sevillatraffic.R
import com.example.sevillatraffic.model.Traffic
import kotlinx.android.synthetic.main.row_traffic_layout.view.*


class MyAdapter(private val myDataset: List<Traffic>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    lateinit var context : Context

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyAdapter.MyViewHolder {
        // create a new view
        context = parent.context
        val rawView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_traffic_layout, parent, false)
        rawView.layoutParams = RecyclerView.LayoutParams(1000, 400)
        rawView.setPadding(20, 0, 0, 20)

        return MyViewHolder(rawView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.txt_direction.text = myDataset[position].direction
        holder.textView.txt_intensity.text = myDataset[position].intensity


        Log.e("PIPO", "ES PIPO " + myDataset[position].source)

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

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}