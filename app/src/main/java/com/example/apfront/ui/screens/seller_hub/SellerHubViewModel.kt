package com.example.apfront.ui.screens.seller_hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// This sealed interface represents all possible states of our hub screen
sealed interface SellerHubUiState {
    object Loading : SellerHubUiState
    data class Success(val restaurant: RestaurantDto) : SellerHubUiState
    object NoRestaurantFound : SellerHubUiState // For when the seller needs to create a restaurant
    data class Error(val message: String) : SellerHubUiState
}

@HiltViewModel
class SellerHubViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SellerHubUiState>(SellerHubUiState.Loading)
    val uiState: StateFlow<SellerHubUiState> = _uiState

    fun checkRestaurantStatus() {
        val token = sessionManager.getAuthToken()

        // If the token is null or empty, we can't proceed.
        if (token.isNullOrEmpty()) {
            _uiState.value = SellerHubUiState.Error("No authentication token found.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SellerHubUiState.Loading
            when (val result = repository.getMyRestaurant(token)) {
                is Resource.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _uiState.value = SellerHubUiState.NoRestaurantFound
                    } else {
                        // If the list is not empty, take the first restaurant
                        val firstRestaurant = result.data.first()
                        _uiState.value = SellerHubUiState.Success(firstRestaurant)
                    }
                }
                is Resource.Error -> {
                    // We check for the specific "404 Not Found" message from the repository
                    if (result.message == "No restaurant found") {
                        _uiState.value = SellerHubUiState.NoRestaurantFound
                    } else {
                        _uiState.value = SellerHubUiState.Error(result.message ?: "An error occurred")
                    }
                }
                else -> {
                    _uiState.value = SellerHubUiState.Error("An unknown state occurred")
                }
            }
        }
    }
}