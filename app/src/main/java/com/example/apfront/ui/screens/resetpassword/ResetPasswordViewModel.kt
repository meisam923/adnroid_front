package com.example.apfront.ui.screens.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.ResetPasswordRequest
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val state = _state.asStateFlow()

    fun resetPassword(email: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = Resource.Loading()
            _state.value = repository.completePasswordReset(ResetPasswordRequest(email, code, newPassword))
        }
    }
}