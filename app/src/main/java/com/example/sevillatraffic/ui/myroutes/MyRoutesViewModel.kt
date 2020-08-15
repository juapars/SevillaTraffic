package com.example.sevillatraffic.ui.myroutes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyRoutesViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "¡Bienvenido a SevillaTraffic! \n Elija como quiere definir su ruta:"
    }
    val text: LiveData<String> = _text
}