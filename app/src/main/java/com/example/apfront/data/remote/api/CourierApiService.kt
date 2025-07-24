package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.remote.dto.UpdateDeliveryStatusRequest
import retrofit2.Response
import retrofit2.http.*

interface CourierApiService {
    @GET("deliveries/available")
    suspend fun getAvailableDeliveries(@Header("Authorization") token: String): Response<List<OrderResponse>>

    @GET("deliveries/history")
    suspend fun getDeliveryHistory(@Header("Authorization") token: String): Response<List<OrderResponse>>

    @PATCH("deliveries/{order_id}")
    suspend fun updateDeliveryStatus(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: Long,
        @Body request: UpdateDeliveryStatusRequest
    ): Response<OrderResponse>
}