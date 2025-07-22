package com.example.apfront.data.repository
import com.example.apfront.data.remote.api.CouponApiService
import com.example.apfront.data.remote.dto.CouponDto
import com.example.apfront.util.Resource
import javax.inject.Inject
class CouponRepositoryImp @Inject constructor(private val api: CouponApiService) : CouponRepository {
    override suspend fun validateCoupon(token: String, code: String): Resource<CouponDto> {
        return try {
            val response = api.validateCoupon("Bearer $token", code)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Invalid coupon code.")
            }
        } catch (e: Exception) {
            Resource.Error("An error occurred: ${e.localizedMessage}")
        }
    }
}