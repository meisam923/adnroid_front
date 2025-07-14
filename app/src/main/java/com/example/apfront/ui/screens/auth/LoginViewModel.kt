package com.example.apfront.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.LoginRequest
import com.example.apfront.data.remote.dto.LoginResponse
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<LoginResponse>>(Resource.Idle())
    val loginState: StateFlow<Resource<LoginResponse>> = _loginState

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val request = LoginRequest(phone = phone, password = password)
            val result = authRepository.login(request)
            _loginState.value = result
        }
    }
}