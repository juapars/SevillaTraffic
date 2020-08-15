package com.example.sevillatraffic.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.sevillatraffic.model.Route
import kotlin.collections.ArrayList

class DBHelper(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VER
){

    companion object{
        private val DATABASE_VER = 1
        private val DATABASE_NAME = "STDB.db"

        // Tabla
        private val TABLE_NAME = "Route"
        private val COL_ID = "Id"
        private val COL_NAME = "Name"
        private val COL_DATE = "Date"
        private val COL_ORIGIN = "Origin"
        private val COL_DEST = "Dest"
//       private val COL_POLYLINE = "Polyline"
        private val COL_NOTSTART = "NotStart"
        private val COL_NOTEND = "NotEnd"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY, $COL_NAME TEXT, $COL_DATE TEXT, $COL_ORIGIN TEXT," +
                " $COL_DEST TEXT, $COL_NOTSTART TEXT, $COL_NOTEND TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
        Log.d("OWO","BASE DE DATOS CREADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA $CREATE_TABLE_QUERY")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
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

                    route.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    route.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                    route.date = cursor.getString(cursor.getColumnIndex(COL_DATE))
                    route.origin = cursor.getString(cursor.getColumnIndex(COL_ORIGIN))
                    route.dest = cursor.getString(cursor.getColumnIndex(COL_DEST))
  //                  route.polyline = cursor.getColumnIndex(COL_POLYLINE) as PolylineOptions
                    route.notStart = cursor.getString(cursor.getColumnIndex(COL_NOTSTART))
                    route.notEnd = cursor.getString(cursor.getColumnIndex(COL_NOTEND))

                    lstRoutes.add(route)
                }while(cursor.moveToNext())
            }
            db.close()

            Log.d("OWO","ALL ROUTES CREADOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
            return lstRoutes
        }

    fun addRoute(route: Route) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID,route.id)
        values.put(COL_NAME,route.name)
        values.put(COL_DATE,route.date)
        values.put(COL_ORIGIN, route.origin)
        values.put(COL_DEST,route.dest)
        //   values.put(COL_POLYLINE, route.polyline)
        values.put(COL_NOTSTART, route.notStart)
        values.put(COL_NOTEND, route.notEnd)

        Log.d("OWO", "INSERTAR  ADD ROUTEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE $db")
        db.insert(TABLE_NAME, null, values)
        Log.d("OWO","TODO INSERTADOOOOOOOOOOOOOOOOOOOOOOOO")
        db.close()
    }

    fun updateRoute(route: Route): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID,route.id)
        values.put(COL_NAME,route.name)
        values.put(COL_DATE,route.date)
        values.put(COL_ORIGIN, route.origin)
        values.put(COL_DEST,route.dest)
   //   values.put(COL_POLYLINE, route.polyline)
        values.put(COL_NOTSTART, route.notStart)
        values.put(COL_NOTEND, route.notEnd)

        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(route.id.toString()))
    }

    fun deleteRoute(route: Route) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(route.id.toString()))
        db.close()
    }

}