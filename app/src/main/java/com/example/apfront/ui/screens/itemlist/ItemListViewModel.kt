package com.example.apfront.ui.screens.itemlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.data.remote.dto.ItemListRequest
import com.example.apfront.data.repository.ItemRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemListUiState(
    val isLoading: Boolean = false,
    val items: List<FoodItemDto> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // This flow listens for changes to the search query
        _uiState
            .map { it.searchQuery } // We only care about the text
            .distinctUntilChanged() // Only proceed if the text has actually changed
            .debounce(500L) // Wait for 500ms after the user stops typing
            .onEach { query ->
                if (query.isNotBlank()) {
                    searchItems(query)
                } else {
                    // If the search box is cleared, clear the results
                    _uiState.update { it.copy(items = emptyList(), error = null) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun searchItems(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = sessionManager.getAuthToken() ?: return@launch
            val request = ItemListRequest(search = query, price = null, keywords = null)

            when (val result = repository.searchItems(token, request)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, items = result.data ?: emptyList()) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }
}