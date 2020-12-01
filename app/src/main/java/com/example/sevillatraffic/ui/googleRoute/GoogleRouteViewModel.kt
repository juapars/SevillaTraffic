package com.example.sevillatraffic.ui.googleRoute

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoogleRouteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Introducir dirección origen y destino:"
    }
    val text: LiveData<String> = _text
}