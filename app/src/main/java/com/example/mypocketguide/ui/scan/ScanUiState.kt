package com.example.mypocketguide.ui.scan

import com.example.mypocketguide.domain.Exhibit

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Loading : ScanUiState
    data class Success(val exhibit: Exhibit) : ScanUiState
    data class Error(val message: String) : ScanUiState
}