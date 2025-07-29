package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.NotificationDto
import com.example.apfront.util.Resource
interface NotificationRepository {
    suspend fun getNotifications(token: String): Resource<List<NotificationDto>>
}