package com.example.apfront.data.repository

import com.example.apfront.data.remote.dto.LoginRequest
import com.example.apfront.data.remote.dto.LoginResponse
import com.example.apfront.data.remote.dto.RegisterRequest
import com.example.apfront.data.remote.dto.RegisterResponse
import com.example.apfront.data.remote.dto.UpdateProfileRequest
import com.example.apfront.data.remote.dto.UserDto
import com.example.apfront.util.Resource

// Using an interface is a good practice for easier testing and swapping implementations
interface AuthRepository {
    suspend fun login(request: LoginRequest): Resource<LoginResponse>
    suspend fun register(request: RegisterRequest): Resource<RegisterResponse>
    suspend fun getProfile(token: String): Resource<UserDto>
    suspend fun updateProfile(token: String, request: UpdateProfileRequest): Resource<Unit>
}