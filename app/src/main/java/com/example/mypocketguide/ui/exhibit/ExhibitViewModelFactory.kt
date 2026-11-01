package com.example.mypocketguide.ui.exhibit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mypocketguide.data.repository.FavoritesRepository
import com.example.mypocketguide.data.settings.SettingsRepository
import com.example.mypocketguide.domain.Exhibit

class ExhibitViewModelFactory(
    private val exhibit: Exhibit,
    private val favoritesRepository: FavoritesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ExhibitViewModel(exhibit, favoritesRepository, settingsRepository) as T
}