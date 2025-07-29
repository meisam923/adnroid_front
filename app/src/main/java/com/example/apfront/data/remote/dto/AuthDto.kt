package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName

// --- Login ---
data class LoginRequest(
    val phone: String,
    val password: String
)

// This DTO now correctly matches your JSON response
data class LoginResponse(
    val message: String,
    @SerializedName("access_token") val accessToken: String, // Looks for "access_token" in JSON
    @SerializedName("refresh_token") val refreshToken: String, // Looks for "refresh_token"
    val user: UserDto
)

// --- User Profile ---
data class UserDto(
    val id: String, // <-- CORRECTED: Changed from Int to String
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val email: String,
    val role: String,
    val address: String,
    val profileImageBase64: String?,
    @SerializedName("bank_info") val bankInfo: BankInfoDto? // Made bank_info nullable just in case
)

data class BankInfoDto(
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("account_number") val accountNumber: String
)
data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    val phone: String,
    val email: String?,
    val password: String,
    val role: String, // "buyer", "seller", or "courier"
    val address: String,
    val profileImageBase64: String?,
    @SerializedName("bank_info") val bankInfo: BankInfoDto?
)

data class RegisterResponse(
    val message: String,
    @SerializedName("user_id") val userId: String,
    val token: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)
data class ForgotPasswordRequest(val email: String)
data class ResetPasswordRequest(val email: String, val code: String, @SerializedName("new_password") val newPassword: String)
