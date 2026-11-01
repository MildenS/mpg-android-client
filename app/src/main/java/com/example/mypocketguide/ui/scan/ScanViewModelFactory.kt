package com.example.mypocketguide.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mypocketguide.data.repository.ExhibitRepository
import com.example.mypocketguide.data.repository.HistoryRepository

class ScanViewModelFactory(
    private val exhibitRepository: ExhibitRepository,
    private val historyRepository: HistoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ScanViewModel(exhibitRepository, historyRepository) as T
}