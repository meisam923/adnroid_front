package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RestaurantApiService {
    @POST("restaurants")
    suspend fun createRestaurant(
        @Header("Authorization") token: String,
        @Body request: CreateRestaurantRequest
    ): Response<RestaurantDto>
}