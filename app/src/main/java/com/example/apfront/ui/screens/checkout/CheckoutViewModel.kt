
package com.example.apfront.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.CartManager
import com.example.apfront.data.remote.dto.*
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.data.repository.CouponRepository
import com.example.apfront.data.repository.OrderRepository
import com.example.apfront.data.repository.PaymentRepository
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val profile: UserDto? = null,
    val calculatedBalance: BigDecimal = BigDecimal.ZERO,
    val restaurantDetails: VendorDetailResponse? = null,
    val couponState: Resource<CouponDto> = Resource.Idle(),
    val orderSubmissionState: Resource<Long?> = Resource.Idle(),
    val deliveryAddress: String = "",
    val errorMessage: String? = null,
    val selectedPaymentMethod: String = "online"
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    val cartManager: CartManager,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository,
    private val vendorRepository: VendorRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val vendorId = cartManager.cart.value.firstOrNull()?.item?.vendorId ?: -1

            if (vendorId == -1) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Cart is empty or invalid.") }
                return@launch
            }

            val profileResult = authRepository.getProfile(token)
            val transactionsResult = paymentRepository.getTransactions(token)
            val restaurantResult = vendorRepository.getVendorDetails(token, vendorId)

            if (profileResult is Resource.Success && transactionsResult is Resource.Success && restaurantResult is Resource.Success) {
                val transactions = transactionsResult.data ?: emptyList()
                val balance = calculateBalance(transactions)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profile = profileResult.data,
                        restaurantDetails = restaurantResult.data,
                        deliveryAddress = profileResult.data?.address ?: "",
                        calculatedBalance = balance
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load checkout data.") }
            }
        }
    }

    private fun calculateBalance(transactions: List<TransactionDto>): BigDecimal {
        var balance = BigDecimal.ZERO
        for (tx in transactions) {
            if (tx.status.equals("SUCCESS", ignoreCase = true)) {
                if (tx.type.equals("TOP_UP", ignoreCase = true)) {
                    balance += tx.amount
                } else if (tx.type.equals("PAYMENT", ignoreCase = true) && tx.method.equals("WALLET", ignoreCase = true)) {
                    balance -= tx.amount
                }
            }
        }
        return balance
    }

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

    fun onProceedToPayment() {
        if (_uiState.value.deliveryAddress.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Delivery address cannot be empty.") }
            return
        }

        val total = calculateTotal()
        if (_uiState.value.selectedPaymentMethod == "wallet" &&
            _uiState.value.calculatedBalance < total) {
            _uiState.update { it.copy(errorMessage = "Insufficient wallet balance.") }
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

                if (_uiState.value.selectedPaymentMethod == "wallet") {
                    val paymentResult = paymentRepository.processPayment(token, PaymentRequest(newOrderId, "wallet"))
                    if (paymentResult is Resource.Success) {
                        cartManager.clearCart()
                        _uiState.update { it.copy(isSubmitting = false, orderSubmissionState = Resource.Success(newOrderId)) }
                    } else {
                        _uiState.update { it.copy(isSubmitting = false, errorMessage = paymentResult.message) }
                    }
                } else {
                    _uiState.update { it.copy(isSubmitting = false, orderSubmissionState = Resource.Success(newOrderId)) }
                }
            } else {
                _uiState.update { it.copy(isSubmitting = false, errorMessage = orderResult.message) }
            }
        }
    }

    fun calculateTotal(): BigDecimal {
        val cart = cartManager.cart.value
        val restaurant = _uiState.value.restaurantDetails?.vendor

        val subtotal = cart.sumOf { it.item.price * it.quantity }.toBigDecimal()
        val coupon = (_uiState.value.couponState as? Resource.Success)?.data
        val discount = coupon?.value ?: BigDecimal.ZERO

        val tax = restaurant?.taxFee?.toBigDecimal() ?: BigDecimal.ZERO
        val additional = restaurant?.additionalFee?.toBigDecimal() ?: BigDecimal.ZERO
        val delivery = BigDecimal("5.00")

        val totalWithFees = subtotal + tax + additional + delivery
        return (totalWithFees - discount).coerceAtLeast(BigDecimal.ZERO)
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(orderSubmissionState = Resource.Idle()) }
    }
}