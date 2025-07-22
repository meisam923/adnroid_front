package com.example.apfront.data.repository
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.util.Resource
interface FavoriteRepository {
    suspend fun getFavorites(token: String): Resource<List<VendorRestaurantDto>>
    suspend fun addFavorite(token: String, restaurantId: Int): Resource<Unit>
    suspend fun removeFavorite(token: String, restaurantId: Int): Resource<Unit>
}