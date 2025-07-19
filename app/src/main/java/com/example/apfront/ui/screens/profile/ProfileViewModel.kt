package com.example.apfront.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.BankInfoDto
import com.example.apfront.data.remote.dto.UpdateProfileRequest
import com.example.apfront.data.remote.dto.UserDto
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)
            val token = sessionManager.getAuthToken()
            if (token == null) {
                _uiState.value = ProfileUiState(error = "Not authenticated.")
                return@launch
            }

            when (val result = repository.getProfile(token)) {
                is Resource.Success -> _uiState.value = ProfileUiState(user = result.data)
                is Resource.Error -> _uiState.value = ProfileUiState(error = result.message)
                else -> {}
            }
        }
    }

    // FIX: The updateProfile function now accepts all the new fields
    fun updateProfile(
        fullName: String,
        phone: String,
        email: String,
        address: String,
        bankName: String,
        accountNumber: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, updateSuccess = false, error = null)
            val token = sessionManager.getAuthToken() ?: return@launch

            // Create the bank info DTO if the fields are not blank
            val bankInfo = if (bankName.isNotBlank() || accountNumber.isNotBlank()) {
                BankInfoDto(bankName, accountNumber)
            } else {
                null
            }

            val request = UpdateProfileRequest(
                fullName = fullName,
                phone = phone,
                email = email,
                address = address,
                profileImageBase64 = null, // Image upload not implemented yet
                bankInfo = bankInfo
            )

            when (repository.updateProfile(token, request)) {
                is Resource.Success -> {
                    // Refresh profile data from server after successful update
                    loadProfile()
                    _uiState.value = _uiState.value.copy(updateSuccess = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Update failed")
                }
                else -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}