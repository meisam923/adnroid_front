package com.example.apfront.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentRequest(
    @SerializedName("order_id") val orderId: Long,
    val method: String // "wallet" or "online"
)

data class TransactionResponse(
    val id: Long,
    @SerializedName("order_id") val orderId: Long,
    @SerializedName("user_id") val userId: Long,
    val amount: BigDecimal,
    val method: String,
    val status: String,
    val type: String,
    @SerializedName("created_at") val createdAt: LocalDateTime
)

data class TopUpRequest(
    val amount: BigDecimal
)

data class TransactionDto(
    val id: Long,
    @SerializedName("order_id") val orderId: Long?,
    @SerializedName("user_id") val userId: Long,
    val amount: BigDecimal,
    val method: String?,
    val status: String,
    val type: String,
    @SerializedName("created_at") val createdAt: LocalDateTime
)