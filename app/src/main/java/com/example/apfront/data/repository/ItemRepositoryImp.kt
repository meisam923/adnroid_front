package com.example.apfront.data.repository

import com.example.apfront.data.remote.api.ItemApiService
import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.remote.dto.ItemListRequest
import com.example.apfront.util.Resource
import javax.inject.Inject

class ItemRepositoryImp @Inject constructor(
    private val api: ItemApiService
) : ItemRepository {
    override suspend fun searchItems(token: String, request: ItemListRequest): Resource<List<FoodItemDto>> {
        return try {
            val response = api.searchItems("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to search items: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun getItemDetails(token: String, itemId: Int): Resource<FoodItemDto> {
        return try {
            val response = api.getItemDetails("Bearer $token", itemId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to get item details: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
}