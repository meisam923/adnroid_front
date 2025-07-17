package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RestaurantApiService {
    @POST("restaurants")
    suspend fun createRestaurant(
        @Header("Authorization") token: String,
        @Body request: CreateRestaurantRequest
    ): Response<RestaurantDto>

    @GET("restaurants/mine")
    suspend fun getMyRestaurant(
        @Header("Authorization") token: String
    ): Response<List<RestaurantDto>>

    @PUT("restaurants/{id}")
    suspend fun updateRestaurant(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Body request: CreateRestaurantRequest // Reusing the DTO from creating
    ): Response<RestaurantDto>
}