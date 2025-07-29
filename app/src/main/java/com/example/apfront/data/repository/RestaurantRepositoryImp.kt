package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.RestaurantApiService
import com.example.apfront.data.remote.dto.*
import com.example.apfront.util.Resource
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApiService
) : RestaurantRepository {

    // A helper function to reduce repetitive code
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                // Handle cases with and without a response body
                val body = response.body()
                Resource.Success(body ?: Unit as T)
            } else {
                when (response.code()) {
                    400 -> Resource.Error("error_400_invalid_input")
                    401 -> Resource.Error("error_401_unauthorized")
                    403 -> Resource.Error("error_403_forbidden")
                    404 -> Resource.Error("error_404_not_found")
                    409 -> Resource.Error("error_409_conflict")
                    500 -> Resource.Error("error_500_server_error")
                    else -> Resource.Error("error_unknown")    }                                      }
        } catch (e: IOException) {
            Resource.Error(code = -1, message = "error_network_connection")
        } catch (e: Exception) {
            Resource.Error(code = -2, message = "error_unknown")
        }
    }

    override suspend fun createRestaurant(token: String, request: CreateRestaurantRequest): Resource<RestaurantDto> {
        return safeApiCall { api.createRestaurant("Bearer $token", request) }
    }

    override suspend fun getMyRestaurant(token: String): Resource<List<RestaurantDto>> {
        return try {
            val response = api.getMyRestaurant("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else if (response.code() == 404) {
                Resource.Error(message = "No restaurant found")
            } else {
                when (response.code()) {
                    400 -> Resource.Error("error_400_invalid_input")
                    401 -> Resource.Error("error_401_unauthorized")
                    403 -> Resource.Error("error_403_forbidden")
                    404 -> Resource.Error("error_404_not_found")
                    409 -> Resource.Error("error_409_conflict")
                    500 -> Resource.Error("error_500_server_error")
                    else -> Resource.Error("error_unknown")    }                                      }
        } catch (e: Exception) {
            Resource.Error(code = -1, message = "error_network_connection")
        }
    }

    override suspend fun updateRestaurant(token: String, restaurantId: Int, request: CreateRestaurantRequest): Resource<RestaurantDto> {
        return safeApiCall { api.updateRestaurant("Bearer $token", restaurantId, request) }
    }

    override suspend fun getVendorMenu(token: String, restaurantId: Int): Resource<VendorMenuResponse> {
        return safeApiCall { api.getVendorMenu("Bearer $token", restaurantId) }
    }

    override suspend fun addFoodItem(token: String, restaurantId: Int, request: CreateItemRequest): Resource<ItemDto> {
        return safeApiCall { api.addFoodItem("Bearer $token", restaurantId, request) }
    }

    override suspend fun updateFoodItem(token: String, restaurantId: Int, itemId: Int, request: CreateItemRequest): Resource<ItemDto> {
        return safeApiCall { api.updateFoodItem("Bearer $token", restaurantId, itemId, request) }
    }

    override suspend fun deleteFoodItem(token: String, restaurantId: Int, itemId: Int): Resource<Unit> {
        return safeApiCall { api.deleteFoodItem("Bearer $token", restaurantId, itemId) }
    }

    override suspend fun createMenuCategory(token: String, restaurantId: Int, request: CreateMenuRequest): Resource<Unit> {
        return safeApiCall { api.createMenuCategory("Bearer $token", restaurantId, request) }
    }

    override suspend fun deleteMenuCategory(token: String, restaurantId: Int, title: String): Resource<Unit> {
        return safeApiCall { api.deleteMenuCategory("Bearer $token", restaurantId, title) }
    }

    // --- THIS FUNCTION WAS MISSING ---
    override suspend fun addItemToMenu(token: String, restaurantId: Int, menuTitle: String, request: AddItemToMenuRequest): Resource<Unit> {
        return safeApiCall { api.addItemToMenu("Bearer $token", restaurantId, menuTitle, request) }
    }

    override suspend fun removeItemFromMenu(token: String, restaurantId: Int, menuTitle: String, itemId: Int): Resource<Unit> {
        return safeApiCall { api.removeItemFromMenu("Bearer $token", restaurantId, menuTitle, itemId) }
    }

    override suspend fun getRestaurantOrders(token: String, restaurantId: Int, status: String?, search: String?): Resource<List<OrderDto>> {
        return safeApiCall { api.getRestaurantOrders("Bearer $token", restaurantId, status,search) }
    }

    override suspend fun updateOrderStatus(token: String, orderId: Int, request: UpdateOrderStatusRequest): Resource<Unit> {
        return safeApiCall { api.updateOrderStatus("Bearer $token", orderId, request) }
    }
    override suspend fun submitReplyToReview(token: String, reviewId: Long, request: ReplyReviewDto): Resource<Unit> {
        return safeApiCall { api.submitReplyReview("Bearer $token", reviewId, request) }
    }
}
