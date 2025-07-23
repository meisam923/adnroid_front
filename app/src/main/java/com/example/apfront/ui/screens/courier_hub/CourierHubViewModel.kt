package com.example.apfront.ui.screens.courier_hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.repository.CourierRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourierHubUiState(
    val isLoading: Boolean = false,
    val availableDeliveries: List<OrderResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CourierHubViewModel @Inject constructor(
    private val repository: CourierRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(CourierHubUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAvailableDeliveries()
    }

    fun loadAvailableDeliveries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            when (val result = repository.getAvailableDeliveries(token)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, availableDeliveries = result.data ?: emptyList()) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}