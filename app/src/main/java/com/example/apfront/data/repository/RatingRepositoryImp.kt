package com.example.apfront.data.repository
import com.example.apfront.data.remote.api.RatingApiService
import com.example.apfront.data.remote.dto.*
import com.example.apfront.util.Resource
import javax.inject.Inject
class RatingRepositoryImp @Inject constructor(private val api: RatingApiService) : RatingRepository {
    override suspend fun submitRating(token: String, request: SubmitRatingRequest): Resource<Unit> {
        return try {
            val response = api.submitRating("Bearer $token", request)
            if (response.isSuccessful) Resource.Success(Unit) else Resource.Error("Failed to submit rating")
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
    override suspend fun getItemRatings(token: String, itemId: Int): Resource<ItemRatingsResponse> {
        return try {
            val response = api.getItemRatings("Bearer $token", itemId)
            if (response.isSuccessful && response.body() != null) Resource.Success(response.body()!!)
            else Resource.Error("Failed to get item ratings")
        } catch (e: Exception) { Resource.Error(e.message ?: "An error occurred") }
    }
    override suspend fun getRatingDetails(token: String, ratingId: Long): Resource<RatingDto> {
        return try {
            val response = api.getRatingDetails("Bearer $token", ratingId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to get rating details: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun updateRating(token: String, ratingId: Long, request: UpdateRatingRequest): Resource<Unit> {
        return try {
            val response = api.updateRating("Bearer $token", ratingId, request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to update rating: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun deleteRating(token: String, ratingId: Long): Resource<Unit> {
        return try {
            val response = api.deleteRating("Bearer $token", ratingId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to delete rating: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("An unknown error occurred: ${e.localizedMessage}")
        }
    }
}