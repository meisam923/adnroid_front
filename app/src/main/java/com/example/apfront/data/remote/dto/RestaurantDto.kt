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


data class OrderItemDto(
    val id: Int,
    val name: String,
    @SerializedName("pricePerItem") val pricePerItem: Double, // Use Double for BigDecimal
    @SerializedName("totalPriceForItem") val totalPriceForItem: Double,
    val quantity: Int
)

// --- UPDATED: OrderDto to match your new backend response ---
data class OrderDto(
    val id: Int,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("coupon_id") val couponId: Int?,
    @SerializedName("items") val items: List<OrderItemDto>,
    @SerializedName("raw_price") val rawPrice: Double,
    @SerializedName("tax_fee") val taxFee: Double,
    @SerializedName("additional_fee") val additionalFee: Double,
    @SerializedName("courier_fee") val courierFee: Double,
    @SerializedName("pay_price") val payPrice: Double,
    @SerializedName("courier_id") val courierId: Int?,
    val status: String,
    val restaurantStatus: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("reviewDto") val review: ReviewDto?
)

data class ReviewDto(
    @SerializedName("id")val id: Long,
    val rating: Int?,
    val comment: String?,
    @SerializedName("base64Images") val base64Images: List<String>?,
    @SerializedName("createdAt") val createdAt: String?
)

