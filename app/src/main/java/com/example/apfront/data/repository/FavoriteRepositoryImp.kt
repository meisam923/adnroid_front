package com.example.apfront.data.repository
import com.example.apfront.data.remote.api.FavoriteApiService
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.util.Resource
import javax.inject.Inject
class FavoriteRepositoryImp @Inject constructor(private val api: FavoriteApiService) : FavoriteRepository {
    override suspend fun getFavorites(token: String): Resource<List<VendorRestaurantDto>> {
        return try {
            val response = api.getFavorites("Bearer $token")
            if (response.isSuccessful && response.body() != null) Resource.Success(response.body()!!)
            else Resource.Error("Failed to get favorites")
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
    override suspend fun addFavorite(token: String, restaurantId: Int): Resource<Unit> {
        return try {
            val response = api.addFavorite("Bearer $token", restaurantId)
            if (response.isSuccessful) Resource.Success(Unit) else Resource.Error("Failed to add favorite")
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
    override suspend fun removeFavorite(token: String, restaurantId: Int): Resource<Unit> {
        return try {
            val response = api.removeFavorite("Bearer $token", restaurantId)
            if (response.isSuccessful) Resource.Success(Unit) else Resource.Error("Failed to remove favorite")
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
}