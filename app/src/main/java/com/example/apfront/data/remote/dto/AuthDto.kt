package com.example.apfront.data.remote.dto

// Use the @SerializedName annotation if your Kotlin variable name
// is different from the JSON key in the API.
import com.google.gson.annotations.SerializedName

// --- Login ---
data class LoginRequest(
    val phone: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserDto
)

// --- Register ---
data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val email: String?,
    val password: String,
    val role: String, // "buyer", "seller", or "courier"
    val address: String
)

data class RegisterResponse(
    val message: String,
    @SerializedName("user_id") val userId: String,
    val token: String
)

// --- User Profile ---
data class UserDto(
    val id: Int,
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val email: String,
    val role: String,
    val address: String
)