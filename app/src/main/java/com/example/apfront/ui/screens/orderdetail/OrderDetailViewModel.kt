package com.example.apfront.ui.screens.orderdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.repository.CourierRepository
import com.example.apfront.data.repository.OrderRepository
import com.example.apfront.data.remote.dto.UpdateDeliveryStatusRequest
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailUiState(
    val isLoading: Boolean = false,
    val order: OrderResponse? = null,
    val error: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val courierRepository: CourierRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState = _uiState.asStateFlow()
    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])
    val userRole: String = sessionManager.getUserRole() ?: "BUYER"

    init {
        loadOrderDetails()
    }

    fun loadOrderDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            when (val result = orderRepository.getOrderDetails(token, orderId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, order = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun updateOrderStatus(newStatus: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val request = UpdateDeliveryStatusRequest(status = newStatus.lowercase())
            when (val result = courierRepository.updateDeliveryStatus(token, orderId, request)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, order = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}