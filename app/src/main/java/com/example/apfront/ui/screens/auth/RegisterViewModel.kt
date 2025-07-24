package com.example.apfront.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.RegisterRequest
import com.example.apfront.data.remote.dto.RegisterResponse
import com.example.apfront.data.repository.AuthRepository
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
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<RegisterResponse>>(Resource.Idle())
    val registerState = _registerState.asStateFlow()

    fun registerUser(
        fullName: String,
        phone: String,
        email: String?,
        password: String,
        role: String,
        address: String
    ) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()

            val request = RegisterRequest(
                fullName = fullName,
                phone = phone,
                email = email,
                password = password,
                role = role,
                address = address
            )

            val result = repository.register(request)

            if (result is Resource.Success && result.data != null) {
                sessionManager.saveSession(
                    token = result.data.token,
                    refreshToken = "",
                    role = role
                )
            }

            _registerState.value = result
        }
    }
}