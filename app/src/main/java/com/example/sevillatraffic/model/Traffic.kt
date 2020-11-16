package com.example.sevillatraffic.model

import java.io.Serializable

/*
    Clase con el modelo para la base de datos del tráfico
 */

class Traffic : Serializable {

    var id : Int = 0
    var location : String? = null
    var direction: String? = null
    var intensity: String? = null
    var source: String? = null

    constructor(id: Int, location: String, direction: String, intensity: String, source: String) {
        this.id = id
        this.location = location
        this.direction = direction
        this.intensity = intensity
        this.source = source
    }

    constructor()

    override fun toString(): String {
        return "Traffic detector with placemark id $id"
    }
}