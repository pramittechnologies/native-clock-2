package com.example.sound

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceAssistantManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            tts?.setPitch(1.05f)
            tts?.setSpeechRate(0.95f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            _isReady.value = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            Log.d("VoiceAssistantManager", "TTS Initialized successfully: ${_isReady.value}")
        } else {
            _isReady.value = false
            Log.e("VoiceAssistantManager", "TTS Initialization failed with status: $status")
        }
    }

    fun speakReminder(title: String, timeFormatted: String, description: String = "") {
        val speechText = buildString {
            append("Reminder alert. ")
            append(title)
            append(". Scheduled for ")
            append(timeFormatted)
            append(". ")
            if (description.isNotBlank()) {
                append("Note: ")
                append(description)
            }
        }
        speakText(speechText)
    }

    fun testVoice(title: String = "Project Review Meeting", timeFormatted: String = "3:30 PM") {
        speakReminder(title, timeFormatted, "Prepare presentation slides and agenda.")
    }

    fun speakText(text: String) {
        if (tts == null || !_isReady.value) {
            // Re-init or retry
            return
        }
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "reminder_speech_${System.currentTimeMillis()}")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "reminder_speech")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
