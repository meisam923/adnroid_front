package com.example.apfront.ui.screens.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.CartManager
import com.example.apfront.data.model.CartItem
import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.repository.ItemRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemDetailUiState(
    val isLoading: Boolean = true,
    val item: FoodItemDto? = null,
    val error: String? = null,
    val quantityInCart: Int = 0
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val sessionManager: SessionManager,
    val cartManager: CartManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle["itemId"])
    private val _apiState = MutableStateFlow(ItemDetailUiState())

    val uiState = combine(_apiState, cartManager.cart) { apiState, cart ->
        apiState.copy(
            quantityInCart = cart.find { it.item.id == itemId }?.quantity ?: 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemDetailUiState())

    init {
        loadItemDetails()
    }

    private fun loadItemDetails() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() ?: return@launch
            when (val result = repository.getItemDetails(token, itemId)) {
                is Resource.Success -> _apiState.update { it.copy(isLoading = false, item = result.data) }
                is Resource.Error -> _apiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun onAddItem() {
        uiState.value.item?.let {
            cartManager.addItem(it, it.vendorId)
        }
    }

    fun onRemoveItem() {
        uiState.value.item?.let {
            cartManager.removeItem(it)
        }
    }
}