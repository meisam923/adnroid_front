package com.example.apfront.ui.screens.restaurantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.VendorDetailResponse
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantDetailUiState(
    val isLoading: Boolean = false,
    val restaurantDetails: VendorDetailResponse? = null,
    val error: String? = null
)

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val repository: VendorRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle // Used to get navigation arguments
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val restaurantId: Int = checkNotNull(savedStateHandle["restaurantId"])

    init {
        loadRestaurantDetails()
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