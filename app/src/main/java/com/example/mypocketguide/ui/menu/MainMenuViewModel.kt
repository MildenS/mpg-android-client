package com.example.mypocketguide.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypocketguide.data.repository.HistoryRepository
import com.example.mypocketguide.domain.Exhibit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainMenuViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val history: StateFlow<List<Exhibit>> = historyRepository
        .observeHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clear()
        }
    }
}