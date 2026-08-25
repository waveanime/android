package com.waveanime.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waveanime.data.api.ApiClient
import com.waveanime.data.model.HomeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: HomeResponse) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchHome()
    }

    fun fetchHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = ApiClient.getHome()
                _uiState.value = HomeUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Erreur réseau")
            }
        }
    }
}