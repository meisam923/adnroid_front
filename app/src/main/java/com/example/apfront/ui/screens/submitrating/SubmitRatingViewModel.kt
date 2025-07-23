package com.example.apfront.ui.screens.submitrating
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.SubmitRatingRequest
import com.example.apfront.data.repository.RatingRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubmitRatingUiState(
    val isSubmitting: Boolean = false,
    val submissionState: Resource<Unit> = Resource.Idle()
)

@HiltViewModel
class SubmitRatingViewModel @Inject constructor(
    private val repository: RatingRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val orderId: Long = checkNotNull(savedStateHandle["orderId"])
    private val _uiState = MutableStateFlow(SubmitRatingUiState())
    val uiState = _uiState.asStateFlow()

    fun submitRating(rating: Int, comment: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val request = SubmitRatingRequest(orderId = orderId, rating = rating, comment = comment)
            val result = repository.submitRating(token, request)
            _uiState.update { it.copy(isSubmitting = false, submissionState = result) }
        }
    }
}