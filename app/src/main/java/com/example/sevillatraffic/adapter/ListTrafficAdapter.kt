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
import androidx.recyclerview.widget.RecyclerView
import com.example.sevillatraffic.R
import com.example.sevillatraffic.databinding.FragmentNotificationsBinding
import com.example.sevillatraffic.databinding.RowTrafficLayoutBinding
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import kotlinx.android.synthetic.main.row_layout.view.*
import kotlinx.android.synthetic.main.row_traffic_layout.view.*

class ListTrafficAdapter(activity: Activity, var lstTraffic: List<Traffic>): BaseAdapter() {

    private var inflater: LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var pos: Int = 0
    private var act = activity
    internal lateinit var db: DBHelper
    private lateinit var name : String
    private var _binding: RowTrafficLayoutBinding? = null
    private val binding get() = _binding!!

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val rowView: View = inflater.inflate(R.layout.row_traffic_layout, null)
        db = DBHelper(act)
        pos = position
        rowView.txt_direction.text = lstTraffic[position].direction.toString()
        rowView.txt_intensity.text = lstTraffic[position].intensity.toString()

        this.notifyDataSetChanged()
        return rowView
    }

    override fun getItem(position: Int): Any {
        return lstTraffic[position]
    }

    override fun getItemId(position: Int): Long {
        return lstTraffic[position].id.toLong()
    }

    override fun getCount(): Int {
        return lstTraffic.size
    }

    fun getPosition(): Int{
        return pos
    }
}