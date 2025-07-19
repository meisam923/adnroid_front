package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.LoginResponse // Re-use this DTO
import com.example.apfront.data.remote.dto.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenRefreshApiService {
    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>
}