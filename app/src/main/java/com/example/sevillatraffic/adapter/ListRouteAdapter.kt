package com.example.sevillatraffic.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import com.example.sevillatraffic.R
import com.example.sevillatraffic.model.Route
import kotlinx.android.synthetic.main.row_layout.view.*

class ListRouteAdapter(activity: Activity, var lstRoute: List<Route>): BaseAdapter() {

    private var inflater: LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView: View = inflater.inflate(R.layout.row_layout, null)

        rowView.txt_row_id.text = lstRoute[position].id.toString()
        rowView.txt_name.text = lstRoute[position].name.toString()
        rowView.txt_notStart.text = lstRoute[position].notStart.toString()
        rowView.txt_notEnd.text = lstRoute[position].date.toString()
/*
        rowView.setOnClickListener{
            edt_id.setText(rowView.txt_row_id.toString())
            edt_name.setText(rowView.txt_name.toString())
            edt_email.setText(rowView.txt_email.toString())
        }
        */
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
}