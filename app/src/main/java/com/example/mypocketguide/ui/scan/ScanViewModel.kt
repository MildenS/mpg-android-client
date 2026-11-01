package com.example.mypocketguide.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypocketguide.data.repository.ExhibitRepository
import com.example.mypocketguide.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScanViewModel(
    private val exhibitRepository: ExhibitRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    fun recognize(imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.value = ScanUiState.Loading
            exhibitRepository.recognizeExhibit(imageBytes)
                .onSuccess { exhibit ->
                    // Сохраняем в историю — теперь она появится на Главной
                    historyRepository.add(exhibit)
                    _state.value = ScanUiState.Success(exhibit)
                }
                .onFailure { e ->
                    _state.value = ScanUiState.Error(
                        e.message ?: "Не удалось распознать экспонат"
                    )
                }
        }
    }

    fun reset() {
        _state.value = ScanUiState.Idle
    }
}