package com.example.apfront.ui.screens.restaurantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.remote.dto.VendorDetailResponse
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartItem(
    val item: FoodItemDto,
    val quantity: Int
)

data class RestaurantDetailUiState(
    val isLoading: Boolean = false,
    val restaurantDetails: VendorDetailResponse? = null,
    val error: String? = null,
    val cart: List<CartItem> = emptyList(),
    val cartTotal: Double = 0.0
)

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val repository: VendorRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val restaurantId: Int = checkNotNull(savedStateHandle["restaurantId"])

    init {
        loadRestaurantDetails()
    }

    fun onAddItem(item: FoodItemDto) {
        _uiState.update { currentState ->
            val cart = currentState.cart.toMutableList()
            val existingItem = cart.find { it.item.id == item.id }

            if (existingItem != null) {
                val index = cart.indexOf(existingItem)
                cart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
            } else {
                cart.add(CartItem(item = item, quantity = 1))
            }

            currentState.copy(cart = cart, cartTotal = calculateTotal(cart))
        }
    }

    fun onRemoveItem(item: FoodItemDto) {
        _uiState.update { currentState ->
            val cart = currentState.cart.toMutableList()
            val existingItem = cart.find { it.item.id == item.id }

            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    val index = cart.indexOf(existingItem)
                    cart[index] = existingItem.copy(quantity = existingItem.quantity - 1)
                } else {
                    cart.remove(existingItem)
                }
            }

            currentState.copy(cart = cart, cartTotal = calculateTotal(cart))
        }
    }

    private fun calculateTotal(cart: List<CartItem>): Double {
        return cart.sumOf { it.item.price * it.quantity }
    }

    private fun loadRestaurantDetails() {
        viewModelScope.launch {
            _uiState.value = RestaurantDetailUiState(isLoading = true)
            val token = sessionManager.getAuthToken()
            if (token == null) {
                _uiState.value = RestaurantDetailUiState(error = "Not authenticated.")
                return@launch
            }

            when (val result = repository.getVendorDetails(token, restaurantId)) {
                is Resource.Success -> {
                    _uiState.value = RestaurantDetailUiState(restaurantDetails = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = RestaurantDetailUiState(error = result.message)
                }
                else -> {}
            }
        }
    }
}