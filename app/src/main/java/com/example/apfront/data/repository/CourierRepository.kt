package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.remote.dto.UpdateDeliveryStatusRequest
import com.example.apfront.util.Resource
interface CourierRepository {
    suspend fun getAvailableDeliveries(token: String): Resource<List<OrderResponse>>
    suspend fun getDeliveryHistory(token: String): Resource<List<OrderResponse>>
    suspend fun updateDeliveryStatus(token: String, orderId: Long, request: UpdateDeliveryStatusRequest): Resource<OrderResponse>
}