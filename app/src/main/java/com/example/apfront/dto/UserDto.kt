package com.example.apfront.dto

import com.google.gson.annotations.SerializedName

object UserDto {

    data class LoginRequestDTO(
        @SerializedName("phone") val phone: String,
        @SerializedName("password") val password: String
    )

    data class LoginResponseDTO(
        @SerializedName("message") val message: String,
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("refresh_token") val refreshToken: String,
        @SerializedName("user") val user: UserSchemaDTO
    )

    data class UserSchemaDTO(
        @SerializedName("id") val id: String,
        @SerializedName("full_name") val fullName: String,
        @SerializedName("phone") val phone: String,
        @SerializedName("email") val email: String?,
        @SerializedName("role") val role: String,
        @SerializedName("address") val address: String,
        @SerializedName("profileImageBase64") val profileImageBase64: String?,
        @SerializedName("bank_info") val bankInfo: BankInfoDTO?
    )

    data class BankInfoDTO(
        @SerializedName("bank_name") val bankName: String,
        @SerializedName("account_number") val accountNumber: String
    )
}