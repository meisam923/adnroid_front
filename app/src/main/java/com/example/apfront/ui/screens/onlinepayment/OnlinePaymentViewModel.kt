package com.example.apfront.ui.screens.onlinepayment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.CartManager
import com.example.apfront.data.remote.dto.PaymentRequest
import com.example.apfront.data.repository.PaymentRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnlinePaymentUiState(
    val isProcessing: Boolean = false,
    val paymentState: Resource<Unit> = Resource.Idle()
)

@HiltViewModel
class OnlinePaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager,
    private val cartManager: CartManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])
    private val _uiState = MutableStateFlow(OnlinePaymentUiState())
    val uiState = _uiState.asStateFlow()

    fun onConfirmPayment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val result = paymentRepository.processPayment(token, PaymentRequest(orderId, "online"))

            if (result is Resource.Success) {
                cartManager.clearCart()
                _uiState.update { it.copy(isProcessing = false, paymentState = Resource.Success(Unit)) }
            } else {
                _uiState.update { it.copy(isProcessing = false, paymentState = Resource.Error(result.message ?: "Payment failed")) }
            }
        }
    }
}