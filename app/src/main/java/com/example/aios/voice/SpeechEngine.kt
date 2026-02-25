package com.example.aios.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Bundle
import android.util.Log

class SpeechEngine(
    private val context: Context,
    private val onResult: (String) -> Unit
) {

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun startListening() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("AI_OS", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("AI_OS", "Speech started")
            }

            override fun onResults(results: Bundle?) {
                Log.d("AI_OS", "Results received")

                val matches = results?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )
                matches?.firstOrNull()?.let {
                    onResult(it)
                }
            }

            override fun onError(error: Int) {
                Log.e("AI_OS", "Speech error: $error")
            }

            override fun onEndOfSpeech() {
                Log.d("AI_OS", "Speech ended")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }
}