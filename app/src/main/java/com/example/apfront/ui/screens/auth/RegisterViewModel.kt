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

    fun registerUser(request: RegisterRequest) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val result = repository.register(request)

            // If registration is successful, save the new session
            if (result is Resource.Success && result.data != null) {
                // Your backend's register response has 'token' instead of 'access_token'
                // and doesn't include a refresh token, so we'll pass an empty string.
                sessionManager.saveSession(
                    token = result.data.token,
                    refreshToken = "", // Your DTO doesn't have a refresh token here
                    role = request.role
                )
            }

            _registerState.value = result
        }
    }
}