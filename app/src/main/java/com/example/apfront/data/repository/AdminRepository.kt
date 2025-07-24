package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.*
import com.example.apfront.data.remote.api.CouponDto
import com.example.apfront.data.remote.api.TransactionDto
import com.example.apfront.data.remote.dto.*
import com.example.apfront.util.Resource

interface AdminRepository {

    suspend fun getAllUsers(token: String): Resource<List<AdminUserDto>>
    suspend fun updateUserStatus(token: String, userId: Long, status: String): Resource<Unit>

    suspend fun getAllOrders(token: String, search: String? = null): Resource<List<AdminOrderDto>>
    suspend fun getTransactions(token: String, search: String? = null): Resource<List<TransactionDto>>

    suspend fun getCoupons(token: String): Resource<List<CouponDto>>
    suspend fun getCoupon(token: String, id: String): Resource<CouponDto>
    suspend fun createCoupon(token: String, coupon: CreateCouponRequest): Resource<CouponDto>
    suspend fun updateCoupon(token: String, id: String, coupon: CreateCouponRequest): Resource<CouponDto>
    suspend fun deleteCoupon(token: String, id: String): Resource<Unit>

    suspend fun getAllRestaurantsForAdmin(token: String, search: String?): Resource<List<RestaurantDto>>
    suspend fun updateRestaurantStatus(token: String, restaurantId: Long, newStatus: String): Resource<Unit>
}
