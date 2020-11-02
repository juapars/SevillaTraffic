package com.example.sevillatraffic.adapter

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.sevillatraffic.db.DBHelper
import com.example.sevillatraffic.model.Traffic
import com.example.sevillatraffic.ui.notifications.NotificationsFragment
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class DownloadXmlTask(var first:Boolean, var db: DBHelper, var context: Context) : AsyncTask<String?, Void?, Int>() {

    override fun doInBackground(vararg params: String?): Int? {
        return try {
            var stream: InputStream? = null
            try {
                Log.e("DESCARGA ARCHIVO","EMPIEZA")
                stream =  downloadUrl("http://trafico.sevilla.org/estado-trafico-CGM.kml")
                if (stream != null) {
                    Log.e("DESCARGA ARCHIVO","eENTRA A LOAD TRAFFIC")
                    loadTraffic(stream)
                }

            } finally {
                stream?.close()
            }
            // Descarga correcta
            0
        } catch (e: IOException) {
            // Error de conexión
            Log.e("DESCARGA ARCHIVO E1",e.message)
            1
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            // Error en los datos
            Log.e("DESCARGA ARCHIVO E2",e.message)
            2
        }
    }

    override fun onPostExecute(resultCode: Int) {
        when (resultCode) {
            0 ->
                Log.e("DESCARGA ARCHIVO","SE HA DESCARGADO")

            1 ->                     // TODO: Mostrar los mensajes de error en el lugar oportuno
                Log.w(this.javaClass.name, "Error de conexión")
            2 -> Log.w(this.javaClass.name, "Error en los datos")
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = 10000
        conn.connectTimeout = 15000
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.connect()
        return conn.inputStream
    }

    private fun readTextFile(inputStream: InputStream): String? {
        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var len: Int
        try {
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
        }
        return outputStream.toString()
    }

    private fun loadTraffic(stream: InputStream) {

        val s = readTextFile(stream)
        lateinit var t_id: String
        lateinit var t_location: String
        lateinit var t_direction: String
        lateinit var t_intensity: String
        lateinit var t_source: String
        var doad = 0
        var doc = Jsoup.parse(s, "", Parser.xmlParser())

        var fecha = doc.select("name")[0].text().split(" ")

        Log.e("DESCARGA ARCHIVO ","Actualizado con fecha ${fecha[2]} ${fecha[3]}")

        var cojc = Calendar.getInstance()
        cojc.set(fecha[2].split("/")[2].toInt(),
            fecha[2].split("/")[1].toInt()-1,
            fecha[2].split("/")[0].toInt(),
            fecha[3].split(":")[0].toInt(),
            fecha[3].split(":")[1].toInt())

        //cojc.compareTo(Calendar.getInstance())  Compara lo de dentro con lo de fuera, 0 si son iguales, 1 si lo de dentro es despues, y -1 si es antes


        Log.e("DESCARGA ARCHIVO ", "EL TITULO ES ${cojc.time} y la actual es ${Calendar.getInstance().time}")

        if ((db.allTraffic.count() != doc.select("Placemark").count() - 1) ||
            cojc < Calendar.getInstance()
        ) {
            Log.e("DESCARGA ARCHIVO ","Actualizado con fecha ${fecha[2]} ${fecha[3]}")
            for (p in doc.select("Placemark")) {
                for (c in p.select("description")) {

                    var n = Jsoup.parse(c.text())
                    if (n.select("th")
                            .count() > 5
                    ) {   // && n.select("th")[5].text() == "DETECTORES"
                        t_source = n.select("th")[5].text()

                        if (n.select("td").count() >2) {
                            //Log.e("TRAFFIC","PLACMARK ID ${p.id().split("_")[1]}")
                            t_id = p.id().split("_")[1]
                            //Log.e("TRAFFIC SENTIDO ", " ${n.select("td")[1].text()}")
                            //t_direction = n.select("td")[1].text()

                            var a = 0
                            for (r in n.select("td")) {
                                if (a == 1) {
                                    //Log.e("TRAFFIC TRAFICO ", " ${r.text()}")
                                    t_intensity = r.text()
                                    a = 0
                                }
                                if (a == 2) {
                                    //Log.e("TRAFFIC TRAFICO ", " ${r.text()}")
                                    t_direction = r.text()
                                    a = 0
                                }

                                if (r.text() == "Estado Trafico") a = 1
                                if (r.text() == "Sentido") a = 2
                            }

                            // Log.e("TRAFFIC", "COORDENADAS ${p.select("Coordinates").text()}")
                            t_location = p.select("Coordinates").text()
                        }
                    }
                }

                var traffic = Traffic(t_id.toInt(), t_location, t_direction, t_intensity, t_source)
                if(first){
                    Log.e("DESCARGA ARCHIVO TR ", "SE AÑADE TRAFFIC")
                    db.addTraffic(traffic)
                } else{
                    Log.e("DESCARGA ARCHIVO TR", "SE ACTUALIZA TRAFFIC")
                    db.updateTraffic(traffic)
                }
                doad += 1
            }
        }

    }

}
