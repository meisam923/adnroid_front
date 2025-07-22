package com.example.apfront.data.remote.api
import com.example.apfront.data.remote.dto.CouponDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CouponApiService {
    @GET("coupons")
    suspend fun validateCoupon(
        @Header("Authorization") token: String,
        @Query("coupon_code") code: String
    ): Response<CouponDto>
}