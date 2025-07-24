package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.CourierApiService
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.remote.dto.UpdateDeliveryStatusRequest
import com.example.apfront.util.Resource
import javax.inject.Inject

class CourierRepositoryImp @Inject constructor(
    private val api: CourierApiService
) : CourierRepository {

    override suspend fun getAvailableDeliveries(token: String): Resource<List<OrderResponse>> {
        return try {
            val response = api.getAvailableDeliveries("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch available deliveries: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getDeliveryHistory(token: String): Resource<List<OrderResponse>> {
        return try {
            val response = api.getDeliveryHistory("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch delivery history: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun updateDeliveryStatus(token: String, orderId: Long, request: UpdateDeliveryStatusRequest): Resource<OrderResponse> {
        return try {
            val response = api.updateDeliveryStatus("Bearer $token", orderId, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to update status: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}