package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.remote.dto.SubmitOrderRequest
import com.example.apfront.util.Resource
interface OrderRepository {
    suspend fun submitOrder(token: String, request: SubmitOrderRequest): Resource<OrderResponse>

    suspend fun getOrderHistory(token: String, search: String?, vendor: String?): Resource<List<OrderResponse>>
    suspend fun getOrderDetails(token: String, orderId: Long): Resource<OrderResponse>
}