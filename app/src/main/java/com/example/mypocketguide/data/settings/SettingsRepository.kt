package com.example.mypocketguide.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)

class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    private val keyAutoRead = booleanPreferencesKey("auto_read_enabled")
    private val keySeenPrompt = booleanPreferencesKey("seen_auto_read_prompt")
    private val keySpeechRate = floatPreferencesKey("speech_rate")
    private val keyServerUrl = stringPreferencesKey("server_url")

    val autoReadEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[keyAutoRead] ?: false
    }

    val hasSeenAutoReadPrompt: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[keySeenPrompt] ?: false
    }

    val speechRate: Flow<Float> = dataStore.data.map { prefs ->
        prefs[keySpeechRate] ?: DEFAULT_SPEECH_RATE
    }

    val serverUrl: Flow<String> = dataStore.data.map { prefs ->
        prefs[keyServerUrl] ?: DEFAULT_SERVER_URL
    }

    suspend fun setAutoReadEnabled(enabled: Boolean) {
        dataStore.edit { it[keyAutoRead] = enabled }
    }

    suspend fun setSeenAutoReadPrompt(seen: Boolean) {
        dataStore.edit { it[keySeenPrompt] = seen }
    }

    suspend fun setSpeechRate(rate: Float) {
        dataStore.edit { it[keySpeechRate] = rate }
    }

    suspend fun setServerUrl(url: String) {
        dataStore.edit { it[keyServerUrl] = url }
    }

    companion object {
        const val DEFAULT_SPEECH_RATE = 1.0f
        const val DEFAULT_SERVER_URL = "http://192.168.1.42:8888/"

        // Границы слайдера скорости речи
        const val MIN_SPEECH_RATE = 0.5f
        const val MAX_SPEECH_RATE = 1.5f
    }
}