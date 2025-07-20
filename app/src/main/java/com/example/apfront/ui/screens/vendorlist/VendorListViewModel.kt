package com.example.apfront.ui.screens.vendorlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.VendorListRequest
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorListUiState(
    val isLoading: Boolean = false,
    val restaurants: List<VendorRestaurantDto> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedRating: Double? = null // State for the selected rating filter
)

@OptIn(FlowPreview::class)
@HiltViewModel
class VendorListViewModel @Inject constructor(
    private val vendorRepository: VendorRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorListUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Load the initial list of all restaurants when the screen first appears.
        loadVendors(isInitialLoad = true)
    }

    /**
     * Called by the UI every time the text in the search bar changes.
     */
    fun onSearchQueryChanged(query: String) {
        // Update the UI state immediately so the user sees their typed text.
        _uiState.update { it.copy(searchQuery = query) }

        // Cancel any previous search job that was waiting to run.
        searchJob?.cancel()

        // Start a new job that will wait for a moment before searching.
        searchJob = viewModelScope.launch {
            delay(500L) // Wait for 500ms after the user stops typing.
            loadVendors() // Trigger the search with the new query.
        }
    }

    /**
     * Called by the UI when a rating filter chip is clicked.
     */
    fun onRatingSelected(rating: Double) {
        // If the user taps the same rating again, it deselects the filter.
        val newRating = if (_uiState.value.selectedRating == rating) null else rating

        _uiState.update { it.copy(selectedRating = newRating) }

        // Immediately trigger a new search with the updated rating filter.
        loadVendors()
    }

    /**
     * The main function to fetch vendors from the API, using the current state.
     */
    private fun loadVendors(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            // On initial load, we don't want to show the loading spinner over the whole screen.
            // On subsequent searches, we do.
            if (!isInitialLoad) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            val token = sessionManager.getAuthToken()
            if (token == null) {
                _uiState.update { it.copy(isLoading = false, error = "Authentication token not found.") }
                return@launch
            }

            val currentState = _uiState.value
            val request = VendorListRequest(
                search = currentState.searchQuery.takeIf { it.isNotBlank() },
                keywords = null,
                minRating = currentState.selectedRating
            )

            when (val result = vendorRepository.getVendors(token, request)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, restaurants = result.data ?: emptyList()) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    /**
     * Handles the complete logout process.
     */
    fun logout() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                authRepository.logout(token)
            }
            sessionManager.clearSession()
        }
    }
}