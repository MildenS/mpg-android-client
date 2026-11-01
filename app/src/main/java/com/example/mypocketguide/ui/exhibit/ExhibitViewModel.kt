package com.example.mypocketguide.ui.exhibit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypocketguide.data.repository.FavoritesRepository
import com.example.mypocketguide.data.settings.SettingsRepository
import com.example.mypocketguide.domain.Exhibit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExhibitViewModel(
    private val exhibit: Exhibit,
    private val favoritesRepository: FavoritesRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val isFavorite: StateFlow<Boolean> = favoritesRepository
        .observeIsFavorite(exhibit.id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

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
            initialValue = 1.0f
        )

    fun toggleFavorite() {
        viewModelScope.launch {
            if (isFavorite.value) {
                favoritesRepository.remove(exhibit.id)
            } else {
                favoritesRepository.add(exhibit)
            }
        }
    }
}