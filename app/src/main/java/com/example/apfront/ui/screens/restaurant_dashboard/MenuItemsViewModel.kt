package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.AddItemToMenuRequest
import com.example.apfront.data.remote.dto.CreateMenuRequest
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

    private var currentRestaurantId: Int? = null

    fun loadMenu(restaurantId: Int) {
        currentRestaurantId = restaurantId
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _uiState.value = MenuItemsUiState.Error(401)
            return
        }
        viewModelScope.launch {
            _uiState.value = MenuItemsUiState.Loading
            when (val result = repository.getVendorMenu(token, restaurantId)) {
                is Resource.Success -> _uiState.value = MenuItemsUiState.Success(result.data!!)
                is Resource.Error -> _uiState.value = MenuItemsUiState.Error(result.code)
                else -> {}
            }
        }
    }

    fun deleteFoodItem(itemId: Int) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId
        if (token.isNullOrEmpty() || restaurantId == null) return
        viewModelScope.launch {
            if (repository.deleteFoodItem(token, restaurantId, itemId) is Resource.Success) {
                loadMenu(restaurantId)
            }
        }
    }

    fun createMenuCategory(title: String) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId
        if (token.isNullOrEmpty() || restaurantId == null || title.isBlank()) return
        viewModelScope.launch {
            if (repository.createMenuCategory(token, restaurantId, CreateMenuRequest(title)) is Resource.Success) {
                loadMenu(restaurantId)
            }
        }
    }

    fun deleteMenuCategory(title: String) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId
        if (token.isNullOrEmpty() || restaurantId == null) return
        viewModelScope.launch {
            if (repository.deleteMenuCategory(token, restaurantId, title) is Resource.Success) {
                loadMenu(restaurantId)
            }
        }
    }

    fun addItemToMenu(menuTitle: String, itemId: Int) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId
        if (token.isNullOrEmpty() || restaurantId == null) return
        viewModelScope.launch {
            if (repository.addItemToMenu(token, restaurantId, menuTitle, AddItemToMenuRequest(itemId)) is Resource.Success) {
                loadMenu(restaurantId)
            }
        }
    }

    fun removeItemFromMenu(menuTitle: String, itemId: Int) {
        val token = sessionManager.getAuthToken()
        val restaurantId = currentRestaurantId
        if (token.isNullOrEmpty() || restaurantId == null) return
        viewModelScope.launch {
            if (repository.removeItemFromMenu(token, restaurantId, menuTitle, itemId) is Resource.Success) {
                loadMenu(restaurantId)
            }
        }
    }
}
