package com.example.sevillatraffic.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ha hecho usted mucha caca"
    }
    val text: LiveData<String> = _text
}