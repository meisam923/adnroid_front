package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.*
import com.example.apfront.util.Resource
interface RatingRepository {
    suspend fun submitRating(token: String, request: SubmitRatingRequest): Resource<Unit>
    suspend fun getItemRatings(token: String, itemId: Int): Resource<ItemRatingsResponse>
    suspend fun getRatingDetails(token: String, ratingId: Long): Resource<RatingDto>
    suspend fun updateRating(token: String, ratingId: Long, request: UpdateRatingRequest): Resource<Unit>
    suspend fun deleteRating(token: String, ratingId: Long): Resource<Unit>
}