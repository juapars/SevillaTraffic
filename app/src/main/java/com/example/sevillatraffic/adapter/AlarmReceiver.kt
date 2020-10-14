package com.example.sevillatraffic.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import android.widget.ListView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.sevillatraffic.R
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import com.example.sevillatraffic.ui.notifications.NotificationsFragment
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    internal lateinit var db: DBHelper
    private val fileUrl = "http://trafico.sevilla.org/estado-trafico-CGM.kml"
    private var lstTraffic: List<Traffic> = ArrayList()
    private var lstRoute: List<Route> = ArrayList()
    private var notDesc: String = ""


    override fun onReceive(context: Context?, intent: Intent?) {
        val pm = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:magico")
        wl.acquire()

        db = DBHelper(context)
        DownloadXmlTask(db,context).execute(fileUrl)
        refreshData(context)

        Log.e("ALARMA","SE HA EJECUTADO LA ALARMA, ARCHIVO ACTUALIZADO?")

        wl.release()
    }

    fun setAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        am.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            1000 * 60 * 2.toLong(),
            pi
        ) // Millisec * Second * Minute
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    private fun refreshData(context: Context) {
        lstTraffic = db.allTraffic // db.intenseTraffic
        lstRoute = db.allRoute
        var list: List<Traffic> = arrayListOf()
        for(r in lstRoute){

            if(r.placemarks != "null") {

                var placemarks = parseString(r.placemarks)
                for (t in lstTraffic) {
                    for (a in placemarks) {
                        if (a == t.id && !list.contains(t)) {
                            list = list + t
                        }
                    }
                }
            }
        }


        for(tr in list){
            notDesc += tr.direction + " -> " + tr.intensity + "\n"
        }

        if(notDesc.isEmpty()) notDesc = "Tráfico fluido en tus rutas"

        var builder = NotificationCompat.Builder(context, "1607")
            .setSmallIcon(R.drawable.ic_baseline_traffic_24)
            .setContentTitle("Tráfico actual ")
            .setContentText(notDesc)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notDesc))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(1607,builder.build())

    }


    private fun parseString(list: String?): List<Int> {
        var res = listOf<Int>()
        for(a in list!!.split(",")){
            res = res + a.toInt()
        }
        return res
    }
}