package com.example.apfront.data.remote.api
import com.example.apfront.data.remote.dto.RestaurantDto
import retrofit2.Response
import retrofit2.http.*

interface AdminApiService {

    // === USERS ===
    @GET("admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String,
        ): Response<List<AdminUserDto>>

    @PATCH("admin/users/{id}/status")
    suspend fun updateUserStatus(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body statusRequest: StatusUpdateRequest
    ): Response<Unit>


    // === ORDERS ===
    @GET("admin/orders")
    suspend fun getAllOrders(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null
    ): Response<List<AdminOrderDto>>


    // === TRANSACTIONS ===
    @GET("admin/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null
    ): Response<List<TransactionDto>>


    // === COUPONS ===
    @GET("admin/coupons")
    suspend fun getCoupons(
        @Header("Authorization") token: String
        ): Response<List<CouponDto>>

    @POST("admin/coupons")
    suspend fun createCoupon(
        @Header("Authorization") token: String,
        @Body coupon: CreateCouponRequest): Response<CouponDto>

    @PUT("admin/coupons/{id}")
    suspend fun updateCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body coupon: CreateCouponRequest
    ): Response<CouponDto>

    @GET("admin/coupons/{id}")
    suspend fun getCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String): Response<CouponDto>

    @DELETE("admin/coupons/{id}")
    suspend fun deleteCoupon(
        @Header("Authorization") token: String,
        @Path("id") id: String): Response<Unit>
    // === RESTAURANTS ===
    @GET("admin/restaurants")
    suspend fun getAllRestaurantsForAdmin(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null
    ): Response<List<RestaurantDto>>

    @PATCH("admin/restaurants/{id}/status")
    suspend fun updateRestaurantStatus(
        @Header("Authorization") token: String,
        @Path("id") userId: Long,
        @Body statusRequest: StatusUpdateRequest
    ): Response<Unit>
}
