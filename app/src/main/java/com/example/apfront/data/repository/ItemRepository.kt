package com.example.apfront.data.repository

import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.remote.dto.ItemListRequest
import com.example.apfront.util.Resource

interface ItemRepository {
    suspend fun searchItems(token: String, request: ItemListRequest): Resource<List<FoodItemDto>>
    suspend fun getItemDetails(token: String, itemId: Int): Resource<FoodItemDto>
}