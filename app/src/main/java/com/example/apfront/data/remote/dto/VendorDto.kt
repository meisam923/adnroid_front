package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO for the request body of POST /vendors
data class VendorListRequest(
    val search: String?,
    val keywords: List<String>?
)

// DTO for a single restaurant in the response list.
// This matches your #/components/schemas/restaurant
data class VendorRestaurantDto(
    val id: Int,
    val name: String,
    val address: String,
    val category: String?,
    val rating: Double?,
    @SerializedName("logo_url") val logoUrl: String?,
    @SerializedName("is_open") val isOpen: Boolean
)