package com.example.apfront.data.repository
import com.example.apfront.data.remote.api.PaymentApiService
import com.example.apfront.data.remote.dto.PaymentRequest
import com.example.apfront.data.remote.dto.TopUpRequest
import com.example.apfront.data.remote.dto.TransactionDto
import com.example.apfront.data.remote.dto.TransactionResponse
import com.example.apfront.util.Resource
import javax.inject.Inject
class PaymentRepositoryImp @Inject constructor(private val api: PaymentApiService) : PaymentRepository {
    override suspend fun processPayment(token: String, request: PaymentRequest): Resource<TransactionResponse> {
        return try {
            val response = api.processPayment("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Payment failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
    override suspend fun topUpWallet(token: String, request: TopUpRequest): Resource<Unit> {
        return try {
            val response = api.topUpWallet("Bearer $token", request)
            if (response.isSuccessful) Resource.Success(Unit) else Resource.Error("Top-up failed")
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
    override suspend fun getTransactions(token: String): Resource<List<TransactionDto>> {
        return try {
            val response = api.getTransactions("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else { Resource.Error("Failed to fetch transactions") }
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
}