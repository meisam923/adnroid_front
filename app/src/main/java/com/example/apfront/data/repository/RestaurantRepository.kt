package com.example.apfront.data.repository

import com.example.apfront.data.remote.dto.CreateItemRequest
import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.ItemDto
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.data.remote.dto.VendorMenuResponse
import com.example.apfront.util.Resource

interface RestaurantRepository {
    suspend fun createRestaurant(
        token: String,
        request: CreateRestaurantRequest
    ): Resource<RestaurantDto>

    suspend fun getMyRestaurant(token: String): Resource<List<RestaurantDto>>
    suspend fun updateRestaurant(
        token: String,
        restaurantId: Int,
        request: CreateRestaurantRequest
    ): Resource<RestaurantDto>

    suspend fun getVendorMenu(token: String, restaurantId: Int): Resource<VendorMenuResponse>

    suspend fun deleteFoodItem(token: String, restaurantId: Int, itemId: Int): Resource<Unit>

    suspend fun addFoodItem(token: String, restaurantId: Int, request: CreateItemRequest): Resource<ItemDto>
    suspend fun updateFoodItem(token: String, restaurantId: Int, itemId: Int, request: CreateItemRequest): Resource<ItemDto>


}