package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.PaymentRequest
import com.example.apfront.data.remote.dto.TopUpRequest
import com.example.apfront.data.remote.dto.TransactionDto
import com.example.apfront.data.remote.dto.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentApiService {
    @POST("payment/online")
    suspend fun processPayment(
        @Header("Authorization") token: String,
        @Body request: PaymentRequest
    ): Response<TransactionResponse>
    @POST("wallet/top-up")
    suspend fun topUpWallet(
        @Header("Authorization") token: String,
        @Body request: TopUpRequest
    ): Response<Unit>

    @GET("transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): Response<List<TransactionDto>>
}