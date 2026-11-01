package com.example.mypocketguide.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypocketguide.data.remote.NetworkModule
import com.example.mypocketguide.data.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ConnectionState {
    data object Idle : ConnectionState
    data object Checking : ConnectionState
    data object Success : ConnectionState
    data object Failure : ConnectionState
    data object InvalidUrl : ConnectionState
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val autoReadEnabled: StateFlow<Boolean> = settingsRepository
        .autoReadEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val speechRate: StateFlow<Float> = settingsRepository
        .speechRate
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsRepository.DEFAULT_SPEECH_RATE
        )

    val serverUrl: StateFlow<String> = settingsRepository
        .serverUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsRepository.DEFAULT_SERVER_URL
        )

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun setAutoRead(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoReadEnabled(enabled)
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            settingsRepository.setSpeechRate(rate)
        }
    }

    fun saveServerUrl(url: String) {
        viewModelScope.launch {
            val normalized = normalizeUrl(url)
            settingsRepository.setServerUrl(normalized)
            NetworkModule.setBaseUrl(normalized)
            _connectionState.value = ConnectionState.Idle
        }
    }

    fun checkConnection(url: String) {
        viewModelScope.launch {
            val normalized = normalizeUrl(url)
            if (!isValidUrl(normalized)) {
                _connectionState.value = ConnectionState.InvalidUrl
                return@launch
            }
            _connectionState.value = ConnectionState.Checking
            val ok = withContext(Dispatchers.IO) {
                NetworkModule.ping(normalized)
            }
            _connectionState.value = if (ok) {
                ConnectionState.Success
            } else {
                ConnectionState.Failure
            }
        }
    }

    fun resetConnectionState() {
        _connectionState.value = ConnectionState.Idle
    }

    private fun normalizeUrl(url: String): String {
        var u = url.trim()
        if (!u.endsWith("/")) u += "/"
        return u
    }

    private fun isValidUrl(url: String): Boolean {
        return (url.startsWith("http://") || url.startsWith("https://")) &&
                url.length > "http://".length + 1
    }
}