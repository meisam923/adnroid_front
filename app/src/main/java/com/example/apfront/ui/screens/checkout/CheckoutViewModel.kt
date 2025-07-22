package com.example.apfront.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.CartManager
import com.example.apfront.data.remote.dto.*
import com.example.apfront.data.repository.CouponRepository
import com.example.apfront.data.repository.OrderRepository
import com.example.apfront.data.repository.PaymentRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isSubmitting: Boolean = false,
    val couponState: Resource<CouponDto> = Resource.Idle(),
    val orderSubmissionState: Resource<Long?> = Resource.Idle(),
    val deliveryAddress: String = "",
    val errorMessage: String? = null,
    val selectedPaymentMethod: String = "online" // Default to "online"
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    val cartManager: CartManager,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val paymentRepository: PaymentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(deliveryAddress = address, errorMessage = null) }
    }

    fun onPaymentMethodSelected(method: String) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }


    fun applyCoupon(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(couponState = Resource.Loading()) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val result = couponRepository.validateCoupon(token, code)
            _uiState.update { it.copy(couponState = result) }
        }
    }

    fun submitAndPay() {
        if (_uiState.value.deliveryAddress.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Delivery address cannot be empty.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, orderSubmissionState = Resource.Idle()) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val cart = cartManager.cart.value
            if (cart.isEmpty()) {
                _uiState.update { it.copy(isSubmitting = false, errorMessage = "Your cart is empty.") }
                return@launch
            }

            val orderRequest = SubmitOrderRequest(
                deliveryAddress = _uiState.value.deliveryAddress,
                vendorId = cart.first().item.vendorId,
                couponId = (_uiState.value.couponState as? Resource.Success)?.data?.id,
                items = cart.map { SubmitOrderItem(it.item.id, it.quantity) }
            )

            val orderResult = orderRepository.submitOrder(token, orderRequest)

            if (orderResult is Resource.Success && orderResult.data != null) {
                val newOrderId = orderResult.data.id
                // Use the selected payment method from the UI state
                val paymentResult = paymentRepository.processPayment(
                    token,
                    PaymentRequest(newOrderId, _uiState.value.selectedPaymentMethod)
                )

                if (paymentResult is Resource.Success) {
                    cartManager.clearCart()
                    _uiState.update { it.copy(isSubmitting = false, orderSubmissionState = Resource.Success(newOrderId)) }
                } else {
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = paymentResult.message) }
                }
            } else {
                _uiState.update { it.copy(isSubmitting = false, errorMessage = orderResult.message) }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


