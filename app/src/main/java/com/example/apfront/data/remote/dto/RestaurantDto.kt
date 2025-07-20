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
    @SerializedName("additional_fee") val additionalFee: Int,
    val approvalStatus: String
)

// This is the main response object from GET /vendors/{id}
data class VendorMenuResponse(
    val vendor: RestaurantDto,
    @SerializedName("menu_titles") val menuTitles: List<String>,
    @SerializedName("menu_title")val menu: Map<String, List<ItemDto>>?
)

data class CreateMenuRequest(
    val title: String
)

data class AddItemToMenuRequest(
    @SerializedName("item_id") val itemId: Int
)

data class CreateItemRequest(
    val name: String,
    val description: String,
    val price: Long,
    val supply: Int,
    @SerializedName("imageBase64") val image: String?,
    val keywords: List<String>
)

data class ItemDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Long,
    val supply: Int,
    @SerializedName("imageBase64") val image: String?,
    val keywords: List<String>
)

data class UpdateOrderStatusRequest(
    val status: String // e.g., "accepted", "rejected", "served"
)

data class OrderDto(
    val id: Int,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("pay_price") val payPrice: Int,
    val status: String
    // Add any other fields you need from the order object
)


