package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.NotificationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface NotificationApiService {
    @GET("notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<List<NotificationDto>>
}