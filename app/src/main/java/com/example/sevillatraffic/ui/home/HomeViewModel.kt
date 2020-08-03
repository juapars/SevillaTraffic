package com.example.sevillatraffic.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Empezar con el botón de abajo"
    }
    val text: LiveData<String> = _text
}