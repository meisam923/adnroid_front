package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class SubmitRatingRequest(
    @SerializedName("order_id") val orderId: Long,
    val rating: Int,
    val comment: String,
    val imageBase64: List<String>? = null
)

data class RatingDto(
    val id: Long,
    @SerializedName("item_id") val itemId: Int,
    val rating: Int,
    val comment: String,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("created_at") val createdAt: LocalDateTime
)

data class ItemRatingsResponse(
    @SerializedName("avg_rating") val avgRating: Double,
    val comments: List<RatingDto>
)
data class UpdateRatingRequest(
    val rating: Int,
    val comment: String
)