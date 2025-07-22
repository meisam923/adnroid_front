package com.example.apfront.data.model

import com.example.apfront.data.remote.dto.FoodItemDto


data class CartItem(
    val item: FoodItemDto,
    val quantity: Int
)