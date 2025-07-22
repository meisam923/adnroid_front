package com.example.apfront.data.remote.api
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.remote.dto.SubmitOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApiService {
    @POST("orders")
    suspend fun submitOrder(
        @Header("Authorization") token: String,
        @Body request: SubmitOrderRequest
    ): Response<OrderResponse>

    @GET("orders/history")
    suspend fun getOrderHistory(
        @Header("Authorization") token: String,
        @Query("search") search: String?,
        @Query("vendor") vendor: String?
    ): Response<List<OrderResponse>>

    @GET("orders/{id}")
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): Response<OrderResponse>
}