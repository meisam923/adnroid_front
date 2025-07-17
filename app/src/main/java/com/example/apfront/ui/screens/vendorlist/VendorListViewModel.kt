package com.example.apfront.ui.screens.vendorlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.VendorListRequest
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorListUiState(
    val isLoading: Boolean = false,
    val restaurants: List<VendorRestaurantDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorListViewModel @Inject constructor(
    private val repository: VendorRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadVendors()
    }

    fun loadVendors(search: String? = null, keywords: List<String>? = null) {
        viewModelScope.launch {
            _uiState.value = VendorListUiState(isLoading = true)

            val token = sessionManager.getAuthToken()
            if (token == null) {
                _uiState.value = VendorListUiState(error = "Not logged in.")
                return@launch
            }

            val request = VendorListRequest(search, keywords)
            when (val result = repository.getVendors(token, request)) {
                is Resource.Success -> {
                    _uiState.value = VendorListUiState(restaurants = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _uiState.value = VendorListUiState(error = result.message)
                }
                else -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            println("Session cleared successfully.")
        }
    }
}