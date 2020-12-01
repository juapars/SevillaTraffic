package com.example.sevillatraffic.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sevillatraffic.R
import com.example.sevillatraffic.R.layout
import com.example.sevillatraffic.adapter.AlarmReceiver
import com.example.sevillatraffic.adapter.DownloadXmlTask
import com.example.sevillatraffic.adapter.ListTrafficAdapter
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import java.util.*
import kotlin.collections.ArrayList


class NotificationsFragment : AppCompatDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var notificationsViewModel: NewRouteViewModel
    private var lstTraffic: List<Traffic> = ArrayList()
    private var lstRoute: List<Route> = ArrayList()
    internal lateinit var db: DBHelper
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private val fileUrl = "http://trafico.sevilla.org/estado-trafico-CGM.kml"

    private var notDesc: String = ""
    private var notFluide: String = ""
    private var notModerate: String = ""
    private var notIntense: String = ""

    private var detec: Boolean = false
    private var fluid: Boolean = false
    private var voice: Boolean = false

    private var bluetooth = false

    private lateinit var name: String
    private lateinit var address: String
    private lateinit var threadName: String

    private fun checkConnected() {
        if(BluetoothAdapter.getDefaultAdapter() != null) {
            BluetoothAdapter.getDefaultAdapter()
                .getProfileProxy(requireContext(), serviceListener, BluetoothProfile.HEADSET)

            var n = BluetoothAdapter.getDefaultAdapter()
                .getProfileConnectionState(BluetoothProfile.HEADSET)

            bluetooth = n == 2
        }
    }
    private var serviceListener: ServiceListener = object : ServiceListener {
        override fun onServiceDisconnected(profile: Int) {}
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            for (device in proxy.connectedDevices) {
                name = device.name
                address = device.address
                threadName = Thread.currentThread().name

                Log.e("NOTIFICACIONES EXTRA", "|" + device.name + " | " + device.address + " | " +
                        proxy.getConnectionState(device) + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")"
                )

            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NewRouteViewModel::class.java)
        val root = inflater.inflate(layout.fragment_notifications, container, false)

        checkConnected()

        db = DBHelper(requireContext())


        if(db.allTraffic.isEmpty())  DownloadXmlTask(true, db, requireContext()).execute(fileUrl)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        detec = prefs.getBoolean("detectors", false)
        fluid = prefs.getBoolean("fluid", false)
        voice = prefs.getBoolean("voice", false)

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

      private fun refreshData(traffic: RecyclerView) {
        //  var detector = (activity?.application as GlobalClass).get_enableDetectors()
        //  var fluid = (activity?.application as GlobalClass).get_enableFluid()
          Log.e("NOTIFICATION ESTADO", "$detec  cc $fluid")
          Log.e("NOTIFICATION DOCE", " VEAMOS EL VALOR DE GLOBAL ${voice}")
          lstTraffic = when {
              detec && fluid -> db.allPermitedTraffic
              detec && !fluid -> db.intenseTrafficDetectors
              !detec && !fluid -> db.intenseTrafficOperator
              else ->
                  db.allPermitedTraffic
          } //db.allTraffic

        lstRoute = db.allRoute

        var list: List<Traffic> = arrayListOf()
        for(r in lstRoute){

            var schedule1 = r.notStart?.split(":")
                var startSchedule = Calendar.getInstance()
                    startSchedule.set(Calendar.HOUR_OF_DAY, schedule1?.get(0)?.toInt()!!)
                    startSchedule.set(Calendar.MINUTE, schedule1[1].toInt())

            var schedule2 = r.notEnd?.split(":")
                var endSchedule = Calendar.getInstance()
                endSchedule.set(Calendar.HOUR_OF_DAY, schedule2?.get(0)?.toInt()!!)
                endSchedule.set(Calendar.MINUTE, schedule2[1].toInt())


            if(r.placemarks != "null" && r.placemarks != "" && startSchedule < endSchedule && Calendar.getInstance() < endSchedule
                    && (r.enabled == "True" || r.enabled == "true")) {


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
                    Log.e("NOTIFICATION ALARMA", "SE DESACTIVA LA ALARMA")
                    alarmMgr?.cancel(alarmIntent)
                }

            if(lstRoute.isEmpty() || (r.enabled == "False" || r.enabled == "false")){
                alarmMgr?.cancel(alarmIntent)
            }
        }

          val emptyTraffic: Traffic
          val enabledRoutes = db.enabledRoute

          if(lstRoute.isEmpty() || enabledRoutes.isEmpty()){
              emptyTraffic = Traffic(0,"","No existen rutas activas","Crea una nueva ruta desde el menú o activa alguna existente","")
              list = list + emptyTraffic
          } else if(list.isEmpty()){
              emptyTraffic = Traffic(0,"","Tráfico fluido","No existen alertas de tráfico o es fluido","")
              list = list + emptyTraffic
          }

          viewManager = LinearLayoutManager(requireContext())
          viewAdapter = ListTrafficAdapter(list)

          recyclerView = traffic.apply {

              setHasFixedSize(true)

              layoutManager = viewManager

              // specify an viewAdapter (see also next example)
              adapter = viewAdapter

              addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
          }

          for(tr in list){
              if(tr.intensity?.contains("FLUIDO")!!) notFluide += tr.direction + ", "
              if(tr.intensity?.contains("INTENSO")!!) notIntense += tr.direction + ", "
              if(tr.intensity?.contains("MODERADO")!!) notModerate += tr.direction + ", "

          }

          if(notIntense.isNotEmpty()){
              notDesc += "\n INTENSO: $notIntense."
          }
          if(notModerate.isNotEmpty()){
              notDesc += "\n MODERADO: $notModerate."
          }
          if(notFluide.isNotEmpty()){
              notDesc += "FLUIDO: $notFluide."
          }

          if(notDesc.isEmpty()) notDesc = "Tráfico fluido en tus rutas"


    }

    private fun parseString(list: String?): List<Int> {
        var res = listOf<Int>()
        for(a in list!!.split(",")){
            res = res + a.toInt()
        }
        return res
    }

}