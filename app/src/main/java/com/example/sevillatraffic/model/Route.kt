package com.example.sevillatraffic.model

import com.google.android.gms.maps.model.PolylineOptions
import java.io.Serializable
import java.util.*

class Route : Serializable {

    var id : Int = 0
    var name : String? = null
    var date : String? = null
    var origin: String? = null
    var dest: String? = null
    var notStart: String? = null
    var notEnd: String? = null
    var placemarks: String? = null
    var enabled: String? = null

    constructor(id: Int, name: String, date: String, origin: String, dest: String,
                notStart: String, notEnd: String, placemarks: String, enabled: String) {
        this.id = id
        this.name = name
        this.date = date
        this.origin = origin
        this.dest = dest
        this.notStart = notStart
        this.notEnd = notEnd
        this.placemarks = placemarks
        this.enabled = enabled
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