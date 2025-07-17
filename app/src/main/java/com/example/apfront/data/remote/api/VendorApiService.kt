package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.VendorDetailResponse
import com.example.apfront.data.remote.dto.VendorListRequest
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface VendorApiService {
    @POST("vendors")
    suspend fun getVendors(
        @Header("Authorization") token: String,
        @Body request: VendorListRequest
    ): Response<List<VendorRestaurantDto>>

    @GET("vendors/{id}")
    suspend fun getVendorDetails(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int
    ): Response<VendorDetailResponse>
}