package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime

data class SubmitOrderItem(
    @SerializedName("item_id") val itemId: Int,
    val quantity: Int
)

data class SubmitOrderRequest(
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("coupon_id") val couponId: Int?,
    val items: List<SubmitOrderItem>
)

data class OrderResponse(
    val id: Long,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("customer_id") val customerId: Long,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("coupon_id") val couponId: Int?,
    @SerializedName("item_ids") val itemIds: List<Int>,
    @SerializedName("raw_price") val rawPrice: BigDecimal,
    @SerializedName("tax_fee") val taxFee: BigDecimal,
    @SerializedName("courier_fee") val courierFee: BigDecimal,
    @SerializedName("additional_fee") val additionalFee: BigDecimal,
    @SerializedName("pay_price") val payPrice: BigDecimal,
    @SerializedName("courier_id") val courierId: Long?,
    val status: String,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    @SerializedName("updated_at") val updatedAt: LocalDateTime
)