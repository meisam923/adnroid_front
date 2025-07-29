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

sealed interface CourierHubState {
    object Loading : CourierHubState
    object NotVerified : CourierHubState
    data class Success(val deliveries: List<OrderResponse>) : CourierHubState
    data class Error(val message: String) : CourierHubState
}

@HiltViewModel
class CourierHubViewModel @Inject constructor(
    private val repository: CourierRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourierHubState>(CourierHubState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadAvailableDeliveries()
    }

    fun loadAvailableDeliveries() {
        viewModelScope.launch {
            _uiState.value = CourierHubState.Loading
            val token = sessionManager.getAuthToken() ?: return@launch

            when (val result = repository.getAvailableDeliveries(token)) {
                is Resource.Success -> {
                    _uiState.value = CourierHubState.Success(result.data ?: emptyList())
                }
                is Resource.Error -> {
                    if (result.message?.contains("verified", ignoreCase = true) == true) {
                        _uiState.value = CourierHubState.NotVerified
                    } else {
                        _uiState.value = CourierHubState.Error(result.message ?: "An unknown error occurred.")
                    }
                }
                else -> {}
            }
        }
    }
}