package com.example.sevillatraffic.ui.manualRoute

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ManualRouteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Comience el registro de ubicaciones, y cuando termine, pulse en parar y a mostrar mapa"
    }
    val text: LiveData<String> = _text
}