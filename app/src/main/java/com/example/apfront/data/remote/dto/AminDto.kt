package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.BankInfoDto
import com.example.apfront.data.remote.dto.OrderItemDto
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class AdminUserDto(
    @SerializedName("id") val id: String,
    @SerializedName("full_name") val fullName :String,
    @SerializedName("phone") val phone :String,
    @SerializedName("email") val email :String,
    @SerializedName("role") val role :String,
    @SerializedName("address") val address :String,
    @SerializedName("profileImageBase64") val profileImageBase64 :String?,
    @SerializedName("status") val status :String,
    val bankInfoDto: BankInfoDto?
    )
data class AdminBankInfoDto(
    @SerializedName("bank_name") val bankName :String,
    @SerializedName("account_number") val accountNumber :String
)
data class AdminOrderDto(
    val id: Int,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("vendor_id") val vendorId: Int,
    @SerializedName("vendor_name") val vendorName: String,
    @SerializedName("coupon_id") val couponId: Int?,
    @SerializedName("items") val items: List<OrderItemDto>?,
    @SerializedName("raw_price") val rawPrice: Double,
    @SerializedName("tax_fee") val taxFee: Double,
    @SerializedName("additional_fee") val additionalFee: Double,
    @SerializedName("courier_fee") val courierFee: Double,
    @SerializedName("pay_price") val payPrice: Double,
    @SerializedName("courier_id") val courierId: Int?,
    @SerializedName("status") val status: String,
    @SerializedName("restaurantStatus")  val restaurantStatus :String,
    @SerializedName("delivery_Status")  val deliveryStatus :String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
) {
}

data class StatusUpdateRequest(@SerializedName("status") val status: String)

data class CreateCouponRequest(
    @SerializedName("coupon_code") val couponCode: String,
    val type: String, // "fixed" or "percent"
    val value: Double,
    @SerializedName("min_price") val minPrice: Int,
    @SerializedName("user_count") val userCount: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String
)

data class CouponDto(
    val id: Int,
    @SerializedName("coupon_code") val couponCode: String,
    val type: String,
    val value: Double,
    @SerializedName("min_price") val minPrice: BigDecimal,
    @SerializedName("user_count") val userCount: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String
)

data class TransactionDto(
    val id: Int,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("method")val method: String,
    @SerializedName("status")val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("type")  val type:String
)

