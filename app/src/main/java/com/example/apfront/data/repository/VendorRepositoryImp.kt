package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.VendorApiService
import com.example.apfront.data.remote.dto.VendorDetailResponse
import com.example.apfront.data.remote.dto.VendorListRequest
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.util.Resource
import javax.inject.Inject

class VendorRepositoryImp @Inject constructor(
    private val api: VendorApiService
) : VendorRepository {
    override suspend fun getVendors(token: String, request: VendorListRequest): Resource<List<VendorRestaurantDto>> {
        return try {
            val response = api.getVendors("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch vendors: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
    override suspend fun getVendorDetails(token: String, restaurantId: Int): Resource<VendorDetailResponse> {
        return try {
            val response = api.getVendorDetails("Bearer $token", restaurantId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch vendor details: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
}