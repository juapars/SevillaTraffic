package com.example.sevillatraffic.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
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
    private var notModerate: String = ""
    private var notIntense: String = ""
    private var cancel = true

    private var voice = false
    private var voiceCar= false
    private var bluetooth = false


    private fun checkConnected(context: Context) {
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, serviceListener, BluetoothProfile.HEADSET)

        var n = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET)

        bluetooth = n ==2
    }
    private var serviceListener: BluetoothProfile.ServiceListener = object :
        BluetoothProfile.ServiceListener {
        override fun onServiceDisconnected(profile: Int) {}
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            for (device in proxy.connectedDevices) {

                Log.e("NOTIFICACIONES EXTRA", "|" + device.name + " | " + device.address + " | " +
                        proxy.getConnectionState(device) + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")"
                )

            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        val pm = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:magico")

        wl.acquire()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        voice = prefs.getBoolean("voice", false)
        voiceCar = prefs.getBoolean("voiceCar", false)

        db = DBHelper(context)
        DownloadXmlTask(false, db, context).execute(fileUrl)
        refreshData(context)



        if(cancel) wl.release()
    }


    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    private fun refreshData(context: Context) {

        lstTraffic =  db.intenseTrafficOperator
        lstRoute = db.allRoute
        var list: List<Traffic> = arrayListOf()
        val route: MutableList<Route> = arrayListOf()

        if(lstRoute.isNotEmpty()) {
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
                if (tr.intensity?.contains("INTENSO")!!) notIntense += tr.direction + ", "
                if (tr.intensity?.contains("MODERADO")!!) notModerate += tr.direction + ", "

            }
            if (notIntense.isNotEmpty()) notDesc += "\n INTENSO: $notIntense."

            if (notModerate.isNotEmpty()) notDesc += "\n MODERADO: $notModerate."


                notDesc.replace(">", " ")

            if(notDesc.isNotEmpty()) {
                var builder = NotificationCompat.Builder(context, "1600")
                    .setSmallIcon(R.drawable.ic_baseline_traffic_24)
                    .setContentTitle("Tráfico actual ")
                    .setContentText(notDesc)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(notDesc))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)


                NotificationManagerCompat.from(context).notify(1600, builder.build())
            }
            checkConnected(context)

            Log.e("RECEIVER","ENTONCES BLUETOOTH ES $bluetooth")

                if (voice) {
                    if(voiceCar || (!voiceCar && bluetooth)){
                        val speechIntent = Intent()
                        speechIntent.setClass(context, VoiceService::class.java)
                        speechIntent.putExtra("MESSAGE", notDesc)
                        context.startService(speechIntent)
                    }
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
