package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.PaymentRequest
import com.example.apfront.data.remote.dto.TopUpRequest
import com.example.apfront.data.remote.dto.TransactionDto
import com.example.apfront.data.remote.dto.TransactionResponse
import com.example.apfront.util.Resource
interface PaymentRepository {
    suspend fun processPayment(token: String, request: PaymentRequest): Resource<TransactionResponse>
    suspend fun topUpWallet(token: String, request: TopUpRequest): Resource<Unit>
    suspend fun getTransactions(token: String): Resource<List<TransactionDto>>
}