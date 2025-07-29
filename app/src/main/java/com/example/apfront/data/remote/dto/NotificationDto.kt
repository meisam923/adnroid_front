package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class NotificationDto(
    val id: String,
    val message: String,
    val timestamp: LocalDateTime,
    @SerializedName("is_read") val isRead: Boolean
)