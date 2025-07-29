package com.example.apfront.data.repository

import com.example.apfront.data.remote.dto.AddItemToMenuRequest
import com.example.apfront.data.remote.dto.CreateItemRequest
import com.example.apfront.data.remote.dto.CreateMenuRequest
import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.ItemDto
import com.example.apfront.data.remote.dto.OrderDto
import com.example.apfront.data.remote.dto.ReplyReviewDto
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.data.remote.dto.UpdateOrderStatusRequest
import com.example.apfront.data.remote.dto.VendorMenuResponse
import com.example.apfront.util.Resource

interface RestaurantRepository {
    suspend fun createRestaurant(token: String, request: CreateRestaurantRequest): Resource<RestaurantDto>
    suspend fun getMyRestaurant(token: String): Resource<List<RestaurantDto>>
    suspend fun updateRestaurant(token: String, restaurantId: Int, request: CreateRestaurantRequest): Resource<RestaurantDto>

    // Menu & Items
    suspend fun getVendorMenu(token: String, restaurantId: Int): Resource<VendorMenuResponse>
    suspend fun addFoodItem(token: String, restaurantId: Int, request: CreateItemRequest): Resource<ItemDto>
    suspend fun updateFoodItem(token: String, restaurantId: Int, itemId: Int, request: CreateItemRequest): Resource<ItemDto>
    suspend fun deleteFoodItem(token: String, restaurantId: Int, itemId: Int): Resource<Unit>
    suspend fun createMenuCategory(token: String, restaurantId: Int, request: CreateMenuRequest): Resource<Unit>
    suspend fun deleteMenuCategory(token: String, restaurantId: Int, title: String): Resource<Unit>
    suspend fun addItemToMenu(token: String, restaurantId: Int, menuTitle: String, request: AddItemToMenuRequest): Resource<Unit>
    suspend fun removeItemFromMenu(token: String, restaurantId: Int, menuTitle: String, itemId: Int): Resource<Unit>

    // Orders
    suspend fun getRestaurantOrders(token: String, restaurantId: Int, status: String?,search : String?): Resource<List<OrderDto>>
    suspend fun updateOrderStatus(token: String, orderId: Int, request: UpdateOrderStatusRequest): Resource<Unit>
    suspend fun submitReplyToReview(token: String, reviewId: Long, request: ReplyReviewDto): Resource<Unit>
    }