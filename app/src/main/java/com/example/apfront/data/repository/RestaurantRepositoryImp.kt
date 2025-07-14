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
                Resource.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}