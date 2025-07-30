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
            _uiState.update { it.copy(isSaving = true, operationSuccess = false, error = null) } // Reset on new attempt
            val token = sessionManager.getAuthToken()
            if (token == null) {
                _uiState.update { it.copy(isSaving = false, error = "Authentication token not found.") }
                return@launch
            }
            val request = UpdateRatingRequest(rating = ratingValue, comment = comment)
            when (val result = repository.updateRating(token, ratingId, request)) { // Assuming updateRating now returns Resource
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false, operationSuccess = true, error = null) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message ?: "Failed to update rating.", operationSuccess = false) }
                }

                is Resource.Idle<*> -> TODO()
                is Resource.Loading<*> -> TODO()
            }
        }
    }

    fun deleteRating() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, operationSuccess = false, error = null) } // Reset on new attempt
            val token = sessionManager.getAuthToken()
            if (token == null) {
                _uiState.update { it.copy(isDeleting = false, error = "Authentication token not found.") }
                return@launch
            }
            when (val result = repository.deleteRating(token, ratingId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isDeleting = false, operationSuccess = true, error = null) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isDeleting = false, error = result.message ?: "Failed to delete rating.", operationSuccess = false) }
                }

                is Resource.Idle<*> -> TODO()
                is Resource.Loading<*> -> TODO()
            }
        }
    }

    // Optional: Call this from LaunchedEffect after popBackStack if needed,
    // or when this screen is navigated away from non-successfully.
    fun resetOperationStatus() {
        _uiState.update { it.copy(operationSuccess = false, error = null) }
    }
}