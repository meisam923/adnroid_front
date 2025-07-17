package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.RestaurantApiService
import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.util.Resource
import java.io.IOException
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApiService
) : RestaurantRepository {

    override suspend fun createRestaurant(
        token: String,
        request: CreateRestaurantRequest
    ): Resource<RestaurantDto> {
        return try {
            val response = api.createRestaurant("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                when (response.code()) {
                    400 -> Resource.Error("error_400_invalid_input")
                    401 -> Resource.Error("error_401_unauthorized")
                    403 -> Resource.Error("error_403_forbidden")
                    404 -> Resource.Error("error_404_not_found")
                    409 -> Resource.Error("error_409_conflict")
                    500 -> Resource.Error("error_500_server_error")
                    else -> Resource.Error("error_unknown")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun getMyRestaurant(token: String): Resource<List<RestaurantDto>> {
        return try {
            val response = api.getMyRestaurant("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else if (response.code() == 401) {
                // This handles the case where the seller has no restaurant yet
                Resource.Error("No restaurant found")
            } else {
                // You can add more specific error codes here later
                Resource.Error("error_unknown")
            }
        } catch (e: IOException) {
            Resource.Error("error_network_connection")
        } catch (e: Exception) {
            Resource.Error("error_unknown")
        }
    }

    override suspend fun updateRestaurant(
        token: String,
        restaurantId: Int,
        request: CreateRestaurantRequest
    ): Resource<RestaurantDto> {
        return try {
            val response = api.updateRestaurant("Bearer $token", restaurantId, request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                when (response.code()) {
                    400 -> Resource.Error("error_400_invalid_input")
                    401 -> Resource.Error("error_401_unauthorized")
                    403 -> Resource.Error("error_403_forbidden")
                    404 -> Resource.Error("error_404_not_found")
                    409 -> Resource.Error("error_409_conflict")
                    500 -> Resource.Error("error_500_server_error")
                    else -> Resource.Error("error_unknown")    }}
        } catch (e: Exception) {
            Resource.Error("error_network_connection")
        }
    }
}