package com.example.apfront.data.repository

import com.example.apfront.data.remote.dto.VendorDetailResponse
import com.example.apfront.data.remote.dto.VendorListRequest
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.util.Resource

interface VendorRepository {
    suspend fun getVendors(token: String, request: VendorListRequest): Resource<List<VendorRestaurantDto>>
    suspend fun getVendorDetails(token: String, restaurantId: Int): Resource<VendorDetailResponse>
}