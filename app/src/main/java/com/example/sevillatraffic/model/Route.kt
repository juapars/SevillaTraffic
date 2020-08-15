package com.example.sevillatraffic.model

import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

class Route {

    var id : Int = 0
    var name : String? = null
    var date : String? = null
    var origin: String? = null
    var dest: String? = null
 //   var polyline: PolylineOptions? = null
    var notStart: String? = null
    var notEnd: String? = null

    constructor(id: Int, name: String, date: String, origin: String, dest: String,
                notStart: String, notEnd: String) {
        this.id = id
        this.name = name
        this.date = date
        this.origin = origin
        this.dest = dest
 //       this.polyline = polyline
        this.notStart = notStart
        this.notEnd = notEnd
    }

    constructor()

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "$name con origen $origin y destino $dest"
    }
}