package com.example.sevillatraffic.adapter

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener
import android.util.Log
import org.w3c.dom.Text


open class VoiceService : Service(), OnInitListener, OnUtteranceCompletedListener {
    private var mTts: TextToSpeech? = null
    private var spokenText: String? = null

    override fun onCreate() {
        mTts = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        spokenText =  intent.getStringExtra("MESSAGE")
        mTts!!.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null)
        return START_STICKY
    }


    override fun onInit(status: Int) {
        mTts!!.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onUtteranceCompleted(uttId: String) {
        stopSelf()
    }

    override fun onDestroy() {
        if (mTts != null) {
            mTts!!.stop()
            mTts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }
}