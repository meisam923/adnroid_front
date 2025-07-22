package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.remote.dto.ItemListRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ItemApiService {
    @POST("items")
    suspend fun searchItems(
        @Header("Authorization") token: String,
        @Body request: ItemListRequest
    ): Response<List<FoodItemDto>>

    @GET("items/{id}")
    suspend fun getItemDetails(
        @Header("Authorization") token: String,
        @Path("id") itemId: Int
    ): Response<FoodItemDto>
}