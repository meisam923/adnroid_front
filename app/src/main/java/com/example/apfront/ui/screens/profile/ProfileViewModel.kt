
package com.example.apfront.ui.screens.profile

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.BankInfoDto
import com.example.apfront.data.remote.dto.UpdateProfileRequest
import com.example.apfront.data.remote.dto.UserDto
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.util.LocaleManager
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val selectedImageUri: Uri? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager,
    private val localeManager: LocaleManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(selectedImageUri = uri) }
    }

    fun onLanguageSelected(languageCode: String) {
        localeManager.setLocale(languageCode)
    }

    fun updateProfile(fullName: String, phone: String, email: String, address: String, bankName: String, accountNumber: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, updateSuccess = false, error = null) }
            val token = sessionManager.getAuthToken() ?: return@launch

            // Convert the selected image URI to a Base64 string for the API
            val imageBase64 = _uiState.value.selectedImageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use {
                    Base64.encodeToString(it.readBytes(), Base64.NO_WRAP)
                }
            } ?: _uiState.value.user?.profileImageBase64 // Keep the old image if a new one isn't selected

            val bankInfo = if (bankName.isNotBlank() || accountNumber.isNotBlank()) {
                BankInfoDto(bankName, accountNumber)
            } else { null }

            val request = UpdateProfileRequest(
                fullName = fullName, phone = phone, email = email, address = address,
                profileImageBase64 = imageBase64, bankInfo = bankInfo
            )

            when (val result = repository.updateProfile(token, request)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(updateSuccess = true) }
                    loadProfile() // Refresh profile data after a successful update
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message ?: "Update failed") }
                else -> {}
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val token = sessionManager.getAuthToken()
            val role = sessionManager.getUserRole()

            if (role?.equals("ADMIN", ignoreCase = true) == true) {
                val adminUser = UserDto(
                    id = "admin_id", fullName = "Admin", phone = "admin",
                    email = "admin@app.com", role = "ADMIN", address = "Admin Address",
                    profileImageBase64 = null, bankInfo = null
                )
                _uiState.update { it.copy(isLoading = false, user = adminUser) }
                return@launch
            }

            if (token == null) {
                _uiState.update { it.copy(isLoading = false, error = "Not authenticated.") }
                return@launch
            }

            when (val result = repository.getProfile(token)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, user = result.data) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            if (token != null) {
                repository.logout(token)
            }
            sessionManager.clearSession()
        }
    }

}