package com.example.sevillatraffic.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sevillatraffic.R
import com.example.sevillatraffic.R.layout
import com.example.sevillatraffic.adapter.AlarmReceiver
import com.example.sevillatraffic.adapter.DownloadXmlTask
import com.example.sevillatraffic.adapter.ListTrafficAdapter
import com.example.sevillatraffic.databinding.FragmentNotificationsBinding
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import java.util.*
import kotlin.collections.ArrayList


class NotificationsFragment : AppCompatDialogFragment() {

    private lateinit var notificationsViewModel: NewRouteViewModel
    private var lstTraffic: List<Traffic> = ArrayList()
    private var lstRoute: List<Route> = ArrayList()
    internal lateinit var db: DBHelper
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private val fileUrl = "http://trafico.sevilla.org/estado-trafico-CGM.kml"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NewRouteViewModel::class.java)
        val root = inflater.inflate(layout.fragment_notifications, container, false)

        db = DBHelper(requireContext())


/*
        val binding: FragmentNotificationsBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main)

        binding.user = User("Test", "User")*/

        // descargar aqui el archivo,hacer el refresh data, y setear aqui las alarmas

       // DownloadXmlTask(db, requireContext()).execute(fileUrl)

        alarmMgr = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        }

        refreshData(root.findViewById(R.id.lst_traffic))

        val pullToRefresh: SwipeRefreshLayout = root.findViewById(R.id.swiperefresh)
        pullToRefresh.setOnRefreshListener {
            refreshData(root.findViewById(R.id.lst_traffic))
            pullToRefresh.isRefreshing = false
        }


        return root
    }

      private fun refreshData(traffic: ListView) {
        lstTraffic = db.allTraffic // db.intenseTraffic
        lstRoute = db.allRoute
        var list: List<Traffic> = arrayListOf()
        for(r in lstRoute){
            var time = Date()
            var schedule1 = r.notStart?.split(":")
                var startSchedule = Calendar.getInstance()
                    startSchedule.set(Calendar.HOUR_OF_DAY, schedule1?.get(0)?.toInt()!!)
                    startSchedule.set(Calendar.MINUTE, schedule1[1].toInt())

            var schedule2 = r.notEnd?.split(":")
                var endSchedule = Calendar.getInstance()
                endSchedule.set(Calendar.HOUR_OF_DAY, schedule2?.get(0)?.toInt()!!)
                endSchedule.set(Calendar.MINUTE, schedule2[1].toInt())

            /*(timer?.get(0)?.plus(timer?.get(1))?.toInt()!! < time.hours.toString().plus(
                    time.minutes.toString()
                ).toInt()
                        && time.hours.toString().plus(time.minutes.toString()).toInt() < timer2?.get(
                    0
                )?.plus(timer2?.get(1))?.toInt()!!)*/

            Log.e("MARIHUANA ", (startSchedule < endSchedule).toString() + " Y" + r.placemarks)

                if(r.placemarks != "null" && r.placemarks != "" && startSchedule < endSchedule) {

                // Set the alarm to start
                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, schedule1[0].toInt())
                    set(Calendar.MINUTE, schedule1[1].toInt())
                }

                // setRepeating() lets you specify a precise custom interval--in this case,
                // 2 minutes.
                alarmMgr!!.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    1000 * 60 * 2,
                    alarmIntent
                )

                var placemarks = parseString(r.placemarks)
                for (t in lstTraffic) {
                    for (a in placemarks) {
                        if (a == t.id && !list.contains(t)) {
                            list = list + t
                        }
                    }
                }
            }else{
                Log.e("ALARMA", "SE DESACTIVA LA ALARMA")
                alarmMgr?.cancel(alarmIntent)
            }
        }

        val adapter = ListTrafficAdapter(requireActivity(), list)
        adapter.notifyDataSetChanged()
        traffic.adapter = adapter
    }

    private fun parseString(list: String?): List<Int> {
        var res = listOf<Int>()
        for(a in list!!.split(",")){
            res = res + a.toInt()
        }
        return res
    }

}