package com.example.sevillatraffic.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewRouteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Elija como quiere definir su ruta:"
    }
    val text: LiveData<String> = _text
}