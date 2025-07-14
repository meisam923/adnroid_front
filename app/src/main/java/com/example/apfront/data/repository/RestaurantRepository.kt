package com.example.apfront.data.repository

import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.util.Resource

interface RestaurantRepository {
    suspend fun createRestaurant(token: String, request: CreateRestaurantRequest): Resource<RestaurantDto>
}