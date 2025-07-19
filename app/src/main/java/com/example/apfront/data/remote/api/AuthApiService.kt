package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.LoginRequest
import com.example.apfront.data.remote.dto.LoginResponse
import com.example.apfront.data.remote.dto.RegisterRequest
import com.example.apfront.data.remote.dto.RegisterResponse
import com.example.apfront.data.remote.dto.UpdateProfileRequest
import com.example.apfront.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserDto>

    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<Unit>
}