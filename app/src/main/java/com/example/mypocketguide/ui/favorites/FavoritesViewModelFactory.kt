package com.example.mypocketguide.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mypocketguide.data.repository.FavoritesRepository

class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FavoritesViewModel(favoritesRepository) as T
}