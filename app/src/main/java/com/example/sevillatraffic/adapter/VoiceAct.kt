package com.example.sevillatraffic.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener
import android.util.Log


class VoiceAct : Activity(), TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{

    private var tts: TextToSpeech? = null
    private var msg = ""
    private lateinit var global : GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startingIntent: Intent = this.intent
        msg = startingIntent.getStringExtra("MESSAGE")
        tts = TextToSpeech(this, this)
/*
         tts = TextToSpeech(this) {
            tts?.setOnUtteranceCompletedListener {
                    Log.e("VOICE SERVICE ONCE ", " ENTRAMOS EN UTTERANCE")
                    tts?.shutdown()
                    tts = null
                    finish()
            }
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
    }

    // OnInitListener impl
    override fun onInit(status: Int) {
        global = this.application as GlobalClass


        if(global.get_enableVoice()) {
            tts?.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
        }
        //tts?.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
       // super.onBackPressed()
    }

    // OnUtteranceCompletedListener impl
    override fun onUtteranceCompleted(utteranceId: String?) {
        Log.e("VOICE SERVICE ONCE ", " ENTRAMOS EN UTTERANCE $utteranceId Y $tts")
        tts?.shutdown()
        tts = null
        finish()
    }

}