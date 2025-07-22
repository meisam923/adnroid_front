package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VendorListRequest(
    val search: String?,
    val keywords: List<String>?,
    @SerializedName("min_rating") val minRating: Double?
)

data class VendorRestaurantDto(
    val id: Int,
    val name: String,
    val address: String,
    val category: String?,
    val rating: Double?,
    @SerializedName("logo_url") val logoUrl: String?,
    @SerializedName("is_open") val isOpen: Boolean
)

data class FoodItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("imageBase64") val imageUrl: String?,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("supply") val supply: Int,
    @SerializedName("keywords") val keywords: List<String>
)

data class VendorDetailResponse(
    val vendor: VendorRestaurantDto,
    @SerializedName("menu_titles") val menuTitles: List<String>,

    @SerializedName("menu_title") val menus: Map<String, List<FoodItemDto>>
)