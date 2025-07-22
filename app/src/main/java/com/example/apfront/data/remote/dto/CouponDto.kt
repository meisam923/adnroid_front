package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDate

data class CouponDto(
    val id: Int,
    @SerializedName("coupon_code") val couponCode: String,
    val type: String,
    val value: BigDecimal,
    @SerializedName("min_price") val minPrice: Int,
    @SerializedName("start_date") val startDate: LocalDate,
    @SerializedName("end_date") val endDate: LocalDate
)