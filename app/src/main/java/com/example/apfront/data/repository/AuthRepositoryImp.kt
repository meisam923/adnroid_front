package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.AuthApiService
import com.example.apfront.data.remote.dto.LoginRequest
import com.example.apfront.data.remote.dto.LoginResponse
import com.example.apfront.data.remote.dto.RegisterRequest
import com.example.apfront.data.remote.dto.RegisterResponse
import com.example.apfront.data.remote.dto.UpdateProfileRequest
import com.example.apfront.data.remote.dto.UserDto
import com.example.apfront.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Login Failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun register(request: RegisterRequest): Resource<RegisterResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Registration Failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
    override suspend fun getProfile(token: String): Resource<UserDto> {
        return try {
            val response = api.getProfile("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch profile: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun updateProfile(token: String, request: UpdateProfileRequest): Resource<Unit> {
        return try {
            val response = api.updateProfile("Bearer $token", request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to update profile: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun logout(token: String): Resource<Unit> {
        return try {
            val response = api.logout("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Logout failed on server: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred during logout: ${e.localizedMessage}")
        }
    }
}