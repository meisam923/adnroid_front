package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RatingApiService {
    @POST("ratings")
    suspend fun submitRating(@Header("Authorization") token: String, @Body request: SubmitRatingRequest): Response<Unit>

    @GET("ratings/items/{item_id}")
    suspend fun getItemRatings(@Header("Authorization") token: String, @Path("item_id") itemId: Int): Response<ItemRatingsResponse>

    @GET("ratings/{id}")
    suspend fun getRatingDetails(@Header("Authorization") token: String, @Path("id") ratingId: Long): Response<RatingDto>

    @PUT("ratings/{id}")
    suspend fun updateRating(@Header("Authorization") token: String, @Path("id") ratingId: Long, @Body request: UpdateRatingRequest): Response<Unit>

    @DELETE("ratings/{id}")
    suspend fun deleteRating(@Header("Authorization") token: String, @Path("id") ratingId: Long): Response<Unit>
}