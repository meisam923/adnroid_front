package com.example.apfront.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.LoginRequest
import com.example.apfront.data.remote.dto.LoginResponse
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager // Inject SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Idle())
    val loginState = _loginState.asStateFlow()

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val request = LoginRequest(phone, password)
            val result = repository.login(request)

            // --- FIX: Save the session on successful login ---
            if (result is Resource.Success && result.data != null) {
                sessionManager.saveSession(
                    token = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    role = result.data.user.role
                )
            }
            // --- END OF FIX ---

            _loginState.value = result
        }
    }

    // Helper to get the role for navigation after login
    fun getLoggedInUserRole(): String? {
        return (loginState.value as? Resource.Success)?.data?.user?.role
    }
}