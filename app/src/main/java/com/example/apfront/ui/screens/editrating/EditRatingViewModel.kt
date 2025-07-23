package com.example.apfront.ui.screens.editrating

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.RatingDto
import com.example.apfront.data.remote.dto.UpdateRatingRequest
import com.example.apfront.data.repository.RatingRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditRatingUiState(
    val isLoading: Boolean = true,
    val rating: RatingDto? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val operationSuccess: Boolean = false
)

@HiltViewModel
class EditRatingViewModel @Inject constructor(
    private val repository: RatingRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ratingId: Long = checkNotNull(savedStateHandle["ratingId"])
    private val _uiState = MutableStateFlow(EditRatingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRatingDetails()
    }

    private fun loadRatingDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            when (val result = repository.getRatingDetails(token, ratingId)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, rating = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun updateRating(ratingValue: Int, comment: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val request = UpdateRatingRequest(rating = ratingValue, comment = comment)
            val result = repository.updateRating(token, ratingId, request)
            if (result is Resource.Success) {
                _uiState.update { it.copy(isSaving = false, operationSuccess = true) }
            } else {
                _uiState.update { it.copy(isSaving = false, error = result.message) }
            }
        }
    }

    fun deleteRating() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val result = repository.deleteRating(token, ratingId)
            if (result is Resource.Success) {
                _uiState.update { it.copy(isDeleting = false, operationSuccess = true) }
            } else {
                _uiState.update { it.copy(isDeleting = false, error = result.message) }
            }
        }
    }
}