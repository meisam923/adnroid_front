package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.OrderDto
import com.example.apfront.data.remote.dto.ReplyReviewDto
import com.example.apfront.data.remote.dto.UpdateOrderStatusRequest
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Represents the state of the Orders screen
sealed interface OrdersUiState {
    object Loading : OrdersUiState
    data class Success(val orders: List<OrderDto>) : OrdersUiState
    data class Error(val code: Int?) : OrdersUiState
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private var currentRestaurantId: Int? = null
    private var lastStatus: String? = null
    private var lastSearchQuery: String? = null

    // 🆕 Now supports search query
    fun loadOrders(restaurantId: Int, status: String, searchQuery: String? = null) {
        currentRestaurantId = restaurantId
        lastStatus = status
        lastSearchQuery = searchQuery

        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _uiState.value = OrdersUiState.Error(401)
            return
        }

        viewModelScope.launch {
            _uiState.value = OrdersUiState.Loading
            val result = repository.getRestaurantOrders(token, restaurantId, status, searchQuery)
            when (result) {
                is Resource.Success -> _uiState.value = OrdersUiState.Success(result.data ?: emptyList())
                is Resource.Error -> _uiState.value = OrdersUiState.Error(result.code)
                else -> {}
            }
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String, currentFilter: String) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId
        if (token.isNullOrEmpty() || restaurantId == null) return

        viewModelScope.launch {
            val request = UpdateOrderStatusRequest(status = newStatus)
            val result = repository.updateOrderStatus(token, orderId, request)

            if (result is Resource.Success) {
                // 🆕 Refresh using last search query
                loadOrders(restaurantId, currentFilter, lastSearchQuery)
            }
        }
    }
    fun submitReplyReview(reviewId: Long, reply: String){
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) { _uiState.value = OrdersUiState.Error(401)
            return }
        viewModelScope.launch {
            val request = ReplyReviewDto(reply = reply)
            val result = repository.submitReplyToReview(token, reviewId, request)
            if (result is Resource.Success) {
                // 🆕 Refresh using last search query
                loadOrders(currentRestaurantId!!, lastStatus!!, lastSearchQuery)
            }
        }
    }
}
