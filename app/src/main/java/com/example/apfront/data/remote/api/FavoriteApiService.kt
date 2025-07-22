package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.MessageResponse
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import retrofit2.Response
import retrofit2.http.*

interface FavoriteApiService {
    @GET("favorites")
    suspend fun getFavorites(@Header("Authorization") token: String): Response<List<VendorRestaurantDto>>

    @PUT("favorites/{restaurantId}")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Path("restaurantId") restaurantId: Int
    ): Response<MessageResponse>

    @DELETE("favorites/{restaurantId}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("restaurantId") restaurantId: Int
    ): Response<MessageResponse>
}