package com.example.sevillatraffic.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.sevillatraffic.R
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    internal lateinit var db: DBHelper
    private val fileUrl = "http://trafico.sevilla.org/estado-trafico-CGM.kml"
    private var lstTraffic: List<Traffic> = ArrayList()
    private var lstRoute: List<Route> = ArrayList()
    private var notDesc: String = ""
    private var notFluide: String = ""
    private var notModerate: String = ""
    private var notIntense: String = ""
    private var cancel = true

    private lateinit var global : GlobalClass

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("ALARMA RECEIVER", "ENTRA AL ON RECEIVE")
        val pm = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:magico")

        Log.e("ALARMA RECEIVER", "LLEGA AL RECEIVER")

        wl.acquire()

        Log.e("ALARMA RECEIVER", "CONTEXT ON RECEIVE ALARM $context")

        db = DBHelper(context)
        DownloadXmlTask(false, db, context).execute(fileUrl)
        refreshData(context)

        Log.e("ALARMA RECEIVER", "SE HA EJECUTADO LA ALARMA VALOR CANCEL $cancel")

        if(cancel) wl.release()
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

        global = context.applicationContext as GlobalClass

        Log.e("ALARM RECEIVER DOCE", " VEAMOS LOS CONTEXT ${context.applicationContext}")

        lstTraffic =  db.intenseTrafficOperator
        lstRoute = db.allRoute
        var list: List<Traffic> = arrayListOf()
        val route: MutableList<Route> = arrayListOf()

        Log.e("ALARM RECEIVER", "REFRESH DATA CON ROUTE IF $lstRoute")
        if(lstRoute.isNotEmpty()) {
            Log.e("ALARM RECEIVER", "OSEA QUE ENTRAR ENTRA AL ROUTE")
            for (r in lstRoute) {
                var schedule2 = r.notEnd?.split(":")
                var endSchedule = Calendar.getInstance()
                endSchedule.set(Calendar.HOUR_OF_DAY, schedule2?.get(0)?.toInt()!!)
                endSchedule.set(Calendar.MINUTE, schedule2[1].toInt())

                if (Calendar.getInstance() < endSchedule) {
                    route += r
                }

            }


            for (r in route) {

                if (r.placemarks != "null" && (r.enabled == "True" || r.enabled == "true")) {

                    var placemarks = parseString(r.placemarks)

                    for (t in lstTraffic) {
                        for (a in placemarks) {
                            if (a == t.id && !list.contains(t)) {
                                list = list + t
                            }
                        }
                    }
                }else{
                    cancelAlarm(context)
                }
            }

            for (tr in list) {
                if (tr.intensity?.contains("FLUIDO")!!) notFluide += tr.direction + ", "
                if (tr.intensity?.contains("INTENSO")!!) notIntense += tr.direction + ", "
                if (tr.intensity?.contains("MODERADO")!!) notModerate += tr.direction + ", "

            }

            if (notIntense.isNotEmpty()) {
                notDesc += "\n INTENSO: $notIntense."
            }
            if (notModerate.isNotEmpty()) {
                notDesc += "\n MODERADO: $notModerate."
            }
            if (notFluide.isNotEmpty()) {
                notDesc += "FLUIDO: $notFluide."
            }

            if (notDesc.isEmpty()) notDesc = "Tráfico fluido en tus rutas"

            Log.e("ALARMA RECEIVER AWA", " POR QUE ENTRA SI DEBERIA PARARSE $notDesc")

            notDesc.replace(">", " ")

            var builder = NotificationCompat.Builder(context, "1600")
                .setSmallIcon(R.drawable.ic_baseline_traffic_24)
                .setContentTitle("Tráfico actual ")
                .setContentText(notDesc)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notDesc))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


            NotificationManagerCompat.from(context).notify(1600, builder.build())

            Log.e(
                "ALARMA RECEIVER DOCE",
                "COMPROBEMOS SI ESTA ACTIVADO LA VOZ ${global.get_enableVoice()}"
            )

            if(global.get_enableVoice()) {
    /*           val speechIntent = Intent()
                speechIntent.setClass(context, VoiceAct::class.java)
                speechIntent.putExtra("MESSAGE", notDesc)
                speechIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                speechIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                context.startActivity(speechIntent)*/

                val speechIntent = Intent()
                speechIntent.setClass(context, VoiceService::class.java)
                speechIntent.putExtra("MESSAGE", notDesc)
               // context.startActivity(speechIntent)

                context.startService(speechIntent)

            }

            if (route.size == lstRoute.size) {
                cancel = false
            }
        }else{
            cancelAlarm(context)
        }
    }


    private fun parseString(list: String?): List<Int> {
        var res = listOf<Int>()
        for(a in list!!.split(",")){
            res = res + a.toInt()
        }
        return res
    }
    
}
