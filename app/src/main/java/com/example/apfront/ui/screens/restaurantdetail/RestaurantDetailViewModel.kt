package com.example.apfront.ui.screens.restaurantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.CartManager
import com.example.apfront.data.model.CartItem
import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.remote.dto.VendorDetailResponse
import com.example.apfront.data.repository.FavoriteRepository
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantDetailUiState(
    val isLoading: Boolean = false,
    val restaurantDetails: VendorDetailResponse? = null,
    val error: String? = null,
    val cart: List<CartItem> = emptyList(),
    val isFavorite: Boolean = false
)

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val vendorRepository: VendorRepository,
    private val sessionManager: SessionManager,
    private val favoriteRepository: FavoriteRepository,
    val cartManager: CartManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // This private state holds the data fetched from the API, including the favorite status
    private val _apiState = MutableStateFlow(RestaurantDetailUiState())

    // The final UI state is a combination of the API data and the shared cart data
    val uiState = combine(_apiState, cartManager.cart) { apiState, cart ->
        apiState.copy(cart = cart)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RestaurantDetailUiState()
    )

    private val restaurantId: Int = checkNotNull(savedStateHandle["restaurantId"])

    init {
        loadRestaurantDetails()
        checkIfFavorite()
    }

    fun onAddItem(item: FoodItemDto) {
        cartManager.addItem(item, restaurantId)
    }

    fun onRemoveItem(item: FoodItemDto) {
        cartManager.removeItem(item)
    }

    private fun loadRestaurantDetails() {
        viewModelScope.launch {
            _apiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch

            when (val result = vendorRepository.getVendorDetails(token, restaurantId)) {
                is Resource.Success -> _apiState.update { it.copy(isLoading = false, restaurantDetails = result.data) }
                is Resource.Error -> _apiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    private fun checkIfFavorite() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() ?: return@launch
            val result = favoriteRepository.getFavorites(token) // Get the result first
            if (result is Resource.Success) {
                val isFav = result.data?.any { it.id == restaurantId } == true
                _apiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() ?: return@launch
            val isCurrentlyFavorite = _apiState.value.isFavorite

            val result = if (isCurrentlyFavorite) {
                favoriteRepository.removeFavorite(token, restaurantId)
            } else {
                favoriteRepository.addFavorite(token, restaurantId)
            }

            if (result is Resource.Success) {
                _apiState.update { it.copy(isFavorite = !isCurrentlyFavorite) }
            }
        }
    }
}