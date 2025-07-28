package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.*
import com.example.apfront.data.remote.api.CouponDto
import com.example.apfront.data.remote.api.TransactionDto
import com.example.apfront.data.remote.dto.*
import com.example.apfront.util.Resource
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val api: AdminApiService
) : AdminRepository {

    override suspend fun getAllUsers(token: String): Resource<List<AdminUserDto>> {
        return try {
            val response = api.getAllUsers("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body().orEmpty())
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun updateUserStatus(token: String, userId: String, status: String): Resource<Unit> {
        return try {
            val response = api.updateUserStatus("Bearer $token", userId, StatusUpdateRequest(status))
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun getAllOrders(token: String, search: String?): Resource<List<AdminOrderDto>> {
        return try {
            val response = api.getAllOrders("Bearer $token", search)
            if (response.isSuccessful) {
                Resource.Success(response.body().orEmpty())
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun getTransactions(token: String, search: String?): Resource<List<TransactionDto>> {
        return try {
            val response = api.getTransactions("Bearer $token", search)
            if (response.isSuccessful) {
                Resource.Success(response.body().orEmpty())
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun getCoupons(token: String): Resource<List<CouponDto>> {
        return try {
            val response = api.getCoupons("Bearer $token")
            if (response.isSuccessful) {
                Resource.Success(response.body().orEmpty())
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun getCoupon(token: String, id: String): Resource<CouponDto> {
        return try {
            val response = api.getCoupon("Bearer $token", id)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun createCoupon(token: String, coupon: CreateCouponRequest): Resource<CouponDto> {
        return try {
            val response = api.createCoupon("Bearer $token", coupon)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun updateCoupon(token: String, id: String, coupon: CreateCouponRequest): Resource<CouponDto> {
        return try {
            val response = api.updateCoupon("Bearer $token", id, coupon)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun deleteCoupon(token: String, id: String): Resource<Unit> {
        return try {
            val response = api.deleteCoupon("Bearer $token", id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun getAllRestaurantsForAdmin(token: String, search: String?): Resource<List<RestaurantDto>> {
        return try {
            val response = api.getAllRestaurantsForAdmin("Bearer $token", search)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    override suspend fun updateRestaurantStatus(token: String, restaurantId: Long, newStatus: String): Resource<Unit> {
        return try {
            val request = StatusUpdateRequest(status = newStatus)
            val response = api.updateRestaurantStatus("Bearer $token", restaurantId, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                handleHttpError(response.code())
            }
        } catch (e: Exception) {
            Resource.Error("error_network_connection", -1)
        }
    }

    private fun <T> handleHttpError(code: Int): Resource<T> {
        val message = when (code) {
            400 -> "error_400_invalid_input"
            401 -> "error_401_unauthorized"
            403 -> "error_403_forbidden"
            404 -> "error_404_not_found"
            409 -> "error_409_conflict"
            500 -> "error_500_server_error"
            else -> "error_unknown"
        }
        return Resource.Error(message, code)
    }
}
