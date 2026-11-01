package com.example.mypocketguide.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mypocketguide.data.repository.HistoryRepository

class MainMenuViewModelFactory(
    private val historyRepository: HistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainMenuViewModel(historyRepository) as T
}