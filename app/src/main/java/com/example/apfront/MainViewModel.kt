package com.example.apfront

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// FIX 1: Update AppStartState to hold the user's role upon login.
sealed interface AppStartState {
    object Loading : AppStartState
    data class UserLoggedIn(val role: String) : AppStartState // Changed from object to data class
    object UserLoggedOut : AppStartState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _startState = MutableStateFlow<AppStartState>(AppStartState.Loading)
    val startState: StateFlow<AppStartState> = _startState

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            // FIX 2: Get the user's role from the session manager.
            val role = sessionManager.getUserRole()

            // FIX 3: Check for both token and role.
            if (token.isNullOrEmpty() || role.isNullOrEmpty()) {
                _startState.value = AppStartState.UserLoggedOut
            } else {
                // If both exist, emit the LoggedIn state with the role.
                _startState.value = AppStartState.UserLoggedIn(role)
            }
        }
    }
}