package com.example.apfront.data.remote.dto


import com.google.gson.annotations.SerializedName

// Data to send when creating a new restaurant
data class CreateRestaurantRequest(
    val name: String,
    val address: String,
    val phone: String,
    val logoBase64: String?,
    @SerializedName("tax_fee") val taxFee: Int,
    @SerializedName("additional_fee") val additionalFee: Int
)

// Data you receive back after creating a restaurant
data class RestaurantDto(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val logoBase64: String?,
    @SerializedName("tax_fee") val taxFee: Int,
    @SerializedName("additional_fee") val additionalFee: Int
)