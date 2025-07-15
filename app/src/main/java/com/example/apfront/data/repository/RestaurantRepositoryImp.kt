package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.RestaurantApiService
import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.util.Resource
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val api: RestaurantApiService
) : RestaurantRepository {

    override suspend fun createRestaurant(token: String, request: CreateRestaurantRequest): Resource<RestaurantDto> {
        return try {
            val token1 :String = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxM2I4ZjQzZC1hOTk4LTRlY2QtYjlmMy1hNjg1ZTU2YjAxOWIiLCJlbWFpbCI6ImFudG9pbmVAYmlzdHJvLmNvbSIsInJvbGUiOiJTRUxMRVIiLCJpYXQiOjE3NTI1MDk2ODEsImV4cCI6MTc1MjUxMDU4MX0.NkQWzRn70n2tzHtWjXIRqoAEUaDzlT5wa6GZs2D6EO8"
            val response = api.createRestaurant("Bearer $token1", request)
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
                }            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}