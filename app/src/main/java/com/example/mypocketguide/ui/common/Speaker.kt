package com.example.mypocketguide.ui.common

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

private const val TAG = "Speaker"
private const val GOOGLE_TTS_ENGINE = "com.google.android.tts"

class Speaker(context: Context) {

    private var tts: TextToSpeech? = null

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLanguageSupported = MutableStateFlow(true)
    val isLanguageSupported: StateFlow<Boolean> = _isLanguageSupported.asStateFlow()

    init {
        Log.d(TAG, "=== Speaker init started ===")

        // Какие движки установлены
        val probeTts = TextToSpeech(context.applicationContext) { /* no-op */ }
        try {
            val engines = probeTts.engines
            Log.d(TAG, "Installed TTS engines: ${engines.map { it.name }}")
            Log.d(TAG, "Default TTS engine: ${probeTts.defaultEngine}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enumerate engines", e)
        } finally {
            probeTts.shutdown()
        }

        // Есть ли Google TTS
        val hasGoogle = try {
            context.packageManager.getPackageInfo(GOOGLE_TTS_ENGINE, 0)
            true
        } catch (e: Exception) {
            Log.w(TAG, "Google TTS not found: ${e.message}")
            false
        }
        Log.d(TAG, "hasGoogle=$hasGoogle")

        val requestedEngine = if (hasGoogle) GOOGLE_TTS_ENGINE else null
        Log.d(TAG, "Requesting engine: ${requestedEngine ?: "SYSTEM DEFAULT"}")

        tts = if (requestedEngine != null) {
            TextToSpeech(context.applicationContext, { status ->
                onInit(status)
            }, requestedEngine)
        } else {
            TextToSpeech(context.applicationContext, { status ->
                onInit(status)
            })
        }
    }

    private fun onInit(status: Int) {
        Log.d(TAG, "onInit callback, status=$status")

        if (status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "TTS init FAILED, status=$status")
            _isReady.value = false
            return
        }

        val engine = tts ?: run {
            Log.e(TAG, "tts object is null in onInit")
            return
        }

        Log.d(TAG, "Actual defaultEngine: ${engine.defaultEngine}")
        Log.d(TAG, "Current voice: ${engine.voice}")
        Log.d(TAG, "Current voice locale: ${engine.voice?.locale}")
        Log.d(TAG, "Current voice name: ${engine.voice?.name}")

        engine.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .build()
        )

        // Читаем ТОЛЬКО на языке системы. Не падаем в английский —
        // иначе кириллица будет читаться как мусор («one one»).
        val systemLocale = Locale.getDefault()
        val result = engine.setLanguage(systemLocale)
        val supported = result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED

        Log.d(TAG, "setLanguage(${systemLocale.toLanguageTag()}) → " +
                "result=$result, ok=$supported")

        if (!supported) {
            Log.e(TAG, "System locale not supported. TTS will not read.")
            _isLanguageSupported.value = false
            _isReady.value = false
            return
        }

        Log.d(TAG, "After setLanguage, voice: ${engine.voice}")
        Log.d(TAG, "After setLanguage, voice name: ${engine.voice?.name}")
        Log.d(TAG, "After setLanguage, voice locale: ${engine.voice?.locale}")

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "onStart: $utteranceId")
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "onDone: $utteranceId")
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e(TAG, "onError (deprecated): $utteranceId")
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e(TAG, "onError: $utteranceId, code=$errorCode")
                _isSpeaking.value = false
            }
        })

        _isReady.value = true
        Log.d(TAG, "=== Speaker READY ===")
    }

    fun speak(text: String) {
        Log.d(TAG, "speak() called, isReady=${_isReady.value}, " +
                "isLanguageSupported=${_isLanguageSupported.value}")
        Log.d(TAG, "Text to speak: \"$text\"")
        Log.d(TAG, "Current voice at speak time: ${tts?.voice}")

        if (!_isReady.value || !_isLanguageSupported.value) {
            Log.w(TAG, "speak() aborted — not ready or language unsupported")
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    fun stop() {
        Log.d(TAG, "stop()")
        tts?.stop()
        _isSpeaking.value = false
    }

    fun setSpeechRate(rate: Float) {
        Log.d(TAG, "setSpeechRate($rate)")
        tts?.setSpeechRate(rate)
    }

    fun shutdown() {
        Log.d(TAG, "shutdown()")
        tts?.stop()
        tts?.shutdown()
        tts = null
        _isReady.value = false
        _isSpeaking.value = false
    }

    private companion object {
        const val UTTERANCE_ID = "exhibit-description"
    }
}

@Composable
fun rememberSpeaker(): Speaker {
    val context = LocalContext.current
    val speaker = remember { Speaker(context) }
    DisposableEffect(Unit) {
        onDispose { speaker.shutdown() }
    }
    return speaker
}