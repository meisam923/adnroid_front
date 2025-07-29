package com.example.apfront.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.BankInfoDto
import com.example.apfront.data.remote.dto.RegisterRequest
import com.example.apfront.data.remote.dto.RegisterResponse
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.util.LocaleManager
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager,
    private val localeManager: LocaleManager // Inject the LocaleManager
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>>(Resource.Idle())
    val registerState = _registerState.asStateFlow()

    fun onLanguageSelected(languageCode: String) {
        localeManager.setLocale(languageCode)
    }

    fun registerUser(
        fullName: String,
        phone: String,
        email: String?,
        password: String,
        role: String,
        address: String,
        bankName: String?,
        accountNumber: String?
    ) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()

            val bankInfo = if (!bankName.isNullOrBlank() && !accountNumber.isNullOrBlank()) {
                BankInfoDto(bankName, accountNumber)
            } else {
                null
            }

            val request = RegisterRequest(
                fullName = fullName,
                phone = phone,
                email = email,
                password = password,
                role = role,
                address = address,
                profileImageBase64 = null,
                bankInfo = bankInfo
            )

            val result = repository.register(request)

            if (result is Resource.Success && result.data != null) {
                sessionManager.saveSession(
                    token = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    role = role
                )
            }

            _registerState.value = result
        }
    }
}