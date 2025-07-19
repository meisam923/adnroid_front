package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("full_name") val fullName: String?,
    val phone: String?,
    val email: String?,
    val address: String?,
    val profileImageBase64: String?,
    @SerializedName("bank_info") val bankInfo: BankInfoDto?
)