// Use your actual package name

import com.google.gson.annotations.SerializedName

/**
 * This file contains the Data Transfer Objects (DTOs) needed for authentication.
 * These should exactly match the structure of the JSON your backend API expects and returns.
 */
object UserDto {

    // For the request body of POST /auth/login
    data class LoginRequestDTO(
        @SerializedName("phone") val phone: String,
        @SerializedName("password") val password: String
    )

    // For the successful response from POST /auth/login
    data class LoginResponseDTO(
        @SerializedName("message") val message: String,
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("refresh_token") val refreshToken: String,
        @SerializedName("user") val user: UserSchemaDTO
    )

    // Represents the #/components/schemas/user object
    data class UserSchemaDTO(
        @SerializedName("id") val id: String, // publicId
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