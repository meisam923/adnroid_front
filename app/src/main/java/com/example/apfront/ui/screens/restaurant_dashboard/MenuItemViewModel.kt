package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.VendorMenuResponse
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Represents the state of the Menu & Items screen
sealed interface MenuItemsUiState {
    object Loading : MenuItemsUiState
    data class Success(val menuData: VendorMenuResponse) : MenuItemsUiState
    data class Error(val code: Int?) : MenuItemsUiState
}

@HiltViewModel
class MenuItemsViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuItemsUiState>(MenuItemsUiState.Loading)
    val uiState: StateFlow<MenuItemsUiState> = _uiState.asStateFlow()

    // We store the restaurantId to use for refreshing the list after a delete
    private var currentRestaurantId: Int? = null

    fun loadMenu(restaurantId: Int) {
        currentRestaurantId = restaurantId // Save the ID for later
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _uiState.value = MenuItemsUiState.Error(401) // Unauthorized
            return
        }

        viewModelScope.launch {
            _uiState.value = MenuItemsUiState.Loading
            when (val result = repository.getVendorMenu(token, restaurantId)) {
                is Resource.Success -> {
                    _uiState.value = MenuItemsUiState.Success(result.data!!)
                }
                is Resource.Error -> {
                    _uiState.value = MenuItemsUiState.Error(result.code)
                }
                else -> {}
            }
        }
    }

    // --- ADDED THIS FUNCTION ---
    fun deleteFoodItem(itemId: Int) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId

        if (token.isNullOrEmpty() || restaurantId == null) {
            // Optionally, you can set an error state here to show a message
            return
        }

        viewModelScope.launch {
            // Call the repository to delete the item
            val result = repository.deleteFoodItem(token, restaurantId, itemId)

            // If the deletion was successful, we need to refresh the list
            if (result is Resource.Success) {
                loadMenu(restaurantId) // Reload the menu to show the change
            } else {
                // Optionally, handle the error (e.g., show a Toast)
            }
        }
    }
}
