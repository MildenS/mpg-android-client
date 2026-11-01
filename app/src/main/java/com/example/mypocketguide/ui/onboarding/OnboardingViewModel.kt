package com.example.mypocketguide.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypocketguide.data.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /**
     * null = ещё не прочитали из DataStore (нечего показывать).
     * false = пользователь ещё не отвечал → показываем диалог.
     * true = отвечал когда-то → не показываем.
     */
    val hasSeenPrompt: StateFlow<Boolean?> = settingsRepository
        .hasSeenAutoReadPrompt
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun enableAutoRead() {
        viewModelScope.launch {
            settingsRepository.setAutoReadEnabled(true)
            settingsRepository.setSeenAutoReadPrompt(true)
        }
    }

    fun declineAutoRead() {
        viewModelScope.launch {
            settingsRepository.setAutoReadEnabled(false)
            settingsRepository.setSeenAutoReadPrompt(true)
        }
    }
}