package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.CouponDto
import com.example.apfront.util.Resource
interface CouponRepository {
    suspend fun validateCoupon(token: String, code: String): Resource<CouponDto>
}