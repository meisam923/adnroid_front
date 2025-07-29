package com.example.apfront.data.remote.api

import com.example.apfront.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApiService {

    // --- Restaurant Management ---

    @POST("restaurants")
    suspend fun createRestaurant(
        @Header("Authorization") token: String,
        @Body request: CreateRestaurantRequest
    ): Response<RestaurantDto>

    @GET("restaurants/mine")
    suspend fun getMyRestaurant(
        @Header("Authorization") token: String
    ): Response<List<RestaurantDto>>

    @PUT("restaurants/{id}")
    suspend fun updateRestaurant(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Body request: CreateRestaurantRequest
    ): Response<RestaurantDto>

    // --- Item & Menu Fetching ---

    @GET("vendors/{id}")
    suspend fun getVendorMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int
    ): Response<VendorMenuResponse>

    // --- Item Management ---

    @POST("restaurants/{id}/item")
    suspend fun addFoodItem(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Body request: CreateItemRequest
    ): Response<ItemDto>

    @PUT("restaurants/{id}/item/{item_id}")
    suspend fun updateFoodItem(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("item_id") itemId: Int,
        @Body request: CreateItemRequest
    ): Response<ItemDto>

    @DELETE("restaurants/{id}/item/{item_id}")
    suspend fun deleteFoodItem(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("item_id") itemId: Int
    ): Response<Unit>

    // --- Menu Category Management ---
    @DELETE("restaurants/{id}/menu/{title}")
    suspend fun deleteMenuCategory(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("title") menuTitle: String
    ): Response<Unit>
    @POST("restaurants/{id}/menu")
    suspend fun createMenuCategory(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Body request: CreateMenuRequest
    ): Response<Unit> // Assuming no specific response body

    @PUT("restaurants/{id}/menu/{title}")
    suspend fun addItemToMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("title") menuTitle: String,
        @Body request: AddItemToMenuRequest
    ): Response<Unit>

    @DELETE("restaurants/{id}/menu/{title}/{item_id}")
    suspend fun removeItemFromMenu(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Path("title") menuTitle: String,
        @Path("item_id") itemId: Int
    ): Response<Unit>

    // --- Order Management ---

    @GET("restaurants/{id}/orders")
    suspend fun getRestaurantOrders(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int,
        @Query("status") status: String?,// To filter by status (e.g., "accepted")
        @Query("search") search: String?,
    ): Response<List<OrderDto>>

    @PATCH("restaurants/orders/{order_id}")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: Int,
        @Body request: UpdateOrderStatusRequest
    ): Response<Unit>

    @PATCH("restaurants/{review_id}/reviews")
    suspend fun submitReplyReview(
        @Header("Authorization") token: String,
        @Path("review_id") reviewId: Long,
        @Body request: ReplyReviewDto
    ): Response<Unit>

    @GET("restaurants/{id}/statistics")
    suspend fun getRestaurantStatistics(
        @Header("Authorization") token: String,
        @Path("id") restaurantId: Int
    ): Response<List<IncomeStatistics>>



}
