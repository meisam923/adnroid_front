package com.example.apfront.ui.screens.orderdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.repository.OrderRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderSuccessUiState(
    val isLoading: Boolean = true,
    val order: OrderResponse? = null,
    val error: String? = null
)

@HiltViewModel
class OrderSuccessViewModel @Inject constructor(
    private val repository: OrderRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderSuccessUiState())
    val uiState = _uiState.asStateFlow()

    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])

    init {
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            when (val result = repository.getOrderDetails(token, orderId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, order = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}