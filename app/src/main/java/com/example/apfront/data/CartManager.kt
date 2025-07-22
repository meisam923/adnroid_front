package com.example.apfront.data

import com.example.apfront.data.model.CartItem
import com.example.apfront.data.remote.dto.FoodItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor() {
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart = _cart.asStateFlow()

    fun addItem(itemToAdd: FoodItemDto, vendorId: Int) {
        _cart.update { currentCart ->
            if (currentCart.isEmpty() || currentCart.first().item.vendorId == vendorId) {
                val mutableCart = currentCart.toMutableList()
                val existingItem = mutableCart.find { it.item.id == itemToAdd.id }
                if (existingItem != null) {
                    val index = mutableCart.indexOf(existingItem)
                    mutableCart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
                } else {
                    mutableCart.add(CartItem(item = itemToAdd, quantity = 1))
                }
                mutableCart
            } else {
                println("Starting a new cart because item is from a different restaurant.")
                listOf(CartItem(item = itemToAdd, quantity = 1))
            }
        }
    }

    fun removeItem(itemToRemove: FoodItemDto) {
        _cart.update { currentCart ->
            val mutableCart = currentCart.toMutableList()
            val existingItem = mutableCart.find { it.item.id == itemToRemove.id }
            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    val index = mutableCart.indexOf(existingItem)
                    mutableCart[index] = existingItem.copy(quantity = existingItem.quantity - 1)
                } else {
                    mutableCart.remove(existingItem)
                }
            }
            mutableCart
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }
}