package com.example.apfront.data.repository
import com.example.apfront.data.remote.api.NotificationApiService
import com.example.apfront.data.remote.dto.NotificationDto
import com.example.apfront.util.Resource
import javax.inject.Inject
class NotificationRepositoryImp @Inject constructor(private val api: NotificationApiService) : NotificationRepository {
    override suspend fun getNotifications(token: String): Resource<List<NotificationDto>> {
        return try {
            val response = api.getNotifications("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch notifications: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}