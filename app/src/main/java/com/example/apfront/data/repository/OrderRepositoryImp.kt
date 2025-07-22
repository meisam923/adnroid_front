package com.example.apfront.data.repository
import com.example.apfront.data.remote.api.OrderApiService
import com.example.apfront.data.remote.dto.OrderResponse
import com.example.apfront.data.remote.dto.SubmitOrderRequest
import com.example.apfront.util.Resource
import javax.inject.Inject
class OrderRepositoryImp @Inject constructor(private val api: OrderApiService) : OrderRepository {
    override suspend fun submitOrder(token: String, request: SubmitOrderRequest): Resource<OrderResponse> {
        return try {
            val response = api.submitOrder("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to submit order: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun getOrderHistory(token: String, search: String?, vendor: String?): Resource<List<OrderResponse>> {
        return try {
            val response = api.getOrderHistory("Bearer $token", search, vendor)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch order history: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun getOrderDetails(token: String, orderId: Long): Resource<OrderResponse> {
        return try {
            val response = api.getOrderDetails("Bearer $token", orderId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch order details: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
}