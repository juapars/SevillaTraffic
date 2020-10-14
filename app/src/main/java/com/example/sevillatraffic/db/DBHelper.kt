package com.example.sevillatraffic.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.sevillatraffic.model.Route
import com.example.sevillatraffic.model.Traffic
import kotlinx.android.synthetic.main.edit_route_fragment.*
import kotlin.collections.ArrayList

class DBHelper(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VER
){

    companion object{
        private val DATABASE_VER = 1
        private val DATABASE_NAME = "sevtrafdb.db"

        // Tabla RUTA
        private val TABLE_NAME = "Route"
        private val COL_ID_R = "Id"
        private val COL_NAME = "Name"
        private val COL_DATE = "Date"
        private val COL_ORIGIN = "Origin"
        private val COL_DEST = "Dest"
        private val COL_NOTSTART = "NotStart"
        private val COL_NOTEND = "NotEnd"
        private val COL_PLACEMARKS = "Placemarks"
        private val COL_ENABLED = "True"


        // Tabla ALERTAS
        private val TABLE_NAME_T = "Traffic"
        private val COL_ID_T = "Id"
        private val COL_LOCATION = "Location"
        private val COL_DIRECTION= "Direction"
        private val COL_INTENSITY = "Intensity"
        private val COL_SOURCE = "Source"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME ($COL_ID_R INTEGER PRIMARY KEY, $COL_NAME TEXT, $COL_DATE TEXT, $COL_ORIGIN TEXT," +
                " $COL_DEST TEXT, $COL_NOTSTART TEXT, $COL_NOTEND TEXT, $COL_PLACEMARKS TEXT, $COL_ENABLED TEXT)")

        val CREATE_TABLE_T_QUERY = ("CREATE TABLE $TABLE_NAME_T ($COL_ID_T INTEGER PRIMARY KEY, $COL_LOCATION TEXT, $COL_DIRECTION TEXT," +
                " $COL_INTENSITY TEXT, $COL_SOURCE TEXT)")

        db!!.execSQL(CREATE_TABLE_QUERY)
        db!!.execSQL(CREATE_TABLE_T_QUERY)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_T")
        onCreate(db!!)
    }


    // CRUD

    val allRoute : List<Route>
        get(){
            val lstRoutes = ArrayList<Route>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)

            if(cursor.moveToFirst()){
                do{
                    val route = Route()

                    route.id = cursor.getInt(cursor.getColumnIndex(COL_ID_R))
                    route.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                    route.date = cursor.getString(cursor.getColumnIndex(COL_DATE))
                    route.origin = cursor.getString(cursor.getColumnIndex(COL_ORIGIN))
                    route.dest = cursor.getString(cursor.getColumnIndex(COL_DEST))
                    route.notStart = cursor.getString(cursor.getColumnIndex(COL_NOTSTART))
                    route.notEnd = cursor.getString(cursor.getColumnIndex(COL_NOTEND))
                    route.placemarks = cursor.getString(cursor.getColumnIndex(COL_PLACEMARKS))
                    route.enabled = cursor.getString(cursor.getColumnIndex(COL_ENABLED))

                    lstRoutes.add(route)
                }while(cursor.moveToNext())
            }
            db.close()

            return lstRoutes
        }

    fun addRoute(route: Route) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID_R,route.id)
        values.put(COL_NAME,route.name)
        values.put(COL_DATE,route.date)
        values.put(COL_ORIGIN, route.origin)
        values.put(COL_DEST,route.dest)
        values.put(COL_NOTSTART, route.notStart)
        values.put(COL_NOTEND, route.notEnd)
        values.put(COL_PLACEMARKS, route.placemarks)
        values.put(COL_ENABLED, route.enabled)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateRoute(route: Route): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID_R,route.id)
        values.put(COL_NAME,route.name)
        values.put(COL_DATE,route.date)
        values.put(COL_ORIGIN, route.origin)
        values.put(COL_DEST,route.dest)
        values.put(COL_NOTSTART, route.notStart)
        values.put(COL_NOTEND, route.notEnd)
        values.put(COL_PLACEMARKS, route.placemarks)
        values.put(COL_ENABLED, route.enabled)

        return db.update(TABLE_NAME, values, "$COL_ID_R=?", arrayOf(route.id.toString()))
    }

    fun deleteRoute(id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID_R=?", arrayOf(id))
        db.close()
    }


    val allTraffic : List<Traffic>
        get(){
            val lstTraffic = ArrayList<Traffic>()
            val selectQuery = "SELECT * FROM $TABLE_NAME_T"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)

            if(cursor.moveToFirst()){
                do{
                    val traffic = Traffic()

                    traffic.id = cursor.getInt(cursor.getColumnIndex(COL_ID_T))
                    traffic.location = cursor.getString(cursor.getColumnIndex(COL_LOCATION))
                    traffic.direction = cursor.getString(cursor.getColumnIndex(COL_DIRECTION))
                    traffic.intensity = cursor.getString(cursor.getColumnIndex(COL_INTENSITY))
                    traffic.source = cursor.getString(cursor.getColumnIndex(COL_SOURCE))

                    lstTraffic.add(traffic)
                }while(cursor.moveToNext())
            }
            db.close()

            return lstTraffic
        }

    val intenseTraffic : List<Traffic>
        get(){
            val lstTraffic = ArrayList<Traffic>() //LIKE '%INTENSO' OR INTENSITY LIKE 'INTENSO%' OR INTENSITY LIKE 'MODERADO'
            val selectQuery = "SELECT * FROM $TABLE_NAME_T WHERE (SOURCE LIKE 'OPERADOR%') AND (INTENSITY LIKE '%INTENSO' OR INTENSITY LIKE 'INTENSO%' OR INTENSITY LIKE 'MODERADO')"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)

            if(cursor.moveToFirst()){
                do{
                    val traffic = Traffic()

                    traffic.id = cursor.getInt(cursor.getColumnIndex(COL_ID_T))
                    traffic.location = cursor.getString(cursor.getColumnIndex(COL_LOCATION))
                    traffic.direction = cursor.getString(cursor.getColumnIndex(COL_DIRECTION))
                    traffic.intensity = cursor.getString(cursor.getColumnIndex(COL_INTENSITY))
                    traffic.source = cursor.getString(cursor.getColumnIndex(COL_SOURCE))

                    lstTraffic.add(traffic)
                }while(cursor.moveToNext())
            }
            db.close()

            return lstTraffic
        }

    fun addTraffic(traffic: Traffic) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID_T, traffic.id)
        values.put(COL_LOCATION, traffic.location)
        values.put(COL_DIRECTION, traffic.direction)
        values.put(COL_INTENSITY, traffic.intensity)
        values.put(COL_SOURCE, traffic.source)

        db.insert(TABLE_NAME_T, null, values)
        db.close()
    }

    fun updateTraffic(traffic: Traffic): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_ID_T, traffic.id)
        values.put(COL_LOCATION, traffic.location)
        values.put(COL_DIRECTION, traffic.direction)
        values.put(COL_INTENSITY, traffic.intensity)
        values.put(COL_SOURCE, traffic.source)

        return db.update(TABLE_NAME_T, values, "$COL_ID_T=?", arrayOf(traffic.id.toString()))
    }

    fun deleteTraffic(id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME_T, "$COL_ID_T=?", arrayOf(id))
        db.close()
    }

    fun getTraffic(id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME_T, "$COL_ID_T=?", arrayOf(id))
        db.close()
    }

}