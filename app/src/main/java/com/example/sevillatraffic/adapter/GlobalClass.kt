package com.example.sevillatraffic.adapter

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.example.sevillatraffic.MainActivity

class GlobalClass() : Application() {

    private var _enableDetectors = true
    private var _enableFluid = false
    private var _enableVoice = false

    fun get_enableDetectors(): Boolean {
        return _enableDetectors
    }

    fun set_enableDetectors(_enableDetectors: Boolean) {
        this._enableDetectors = _enableDetectors
    }

    fun get_enableFluid(): Boolean {
        return _enableFluid
    }

    fun set_enableFluid(_enableFluid: Boolean) {
        this._enableFluid = _enableFluid
    }

    fun get_enableVoice(): Boolean {
        return _enableVoice
    }

    fun set_enableVoice(_enableVoice: Boolean) {
        this._enableVoice = _enableVoice
    }
}