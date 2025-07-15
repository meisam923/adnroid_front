package com.example.apfront

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Represents the initial state of the app
sealed interface AppStartState {
    object Loading : AppStartState
    object UserLoggedIn : AppStartState
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
            // Check if a token exists in our session manager
            if (sessionManager.getAuthToken().isNullOrEmpty()) {
                _startState.value = AppStartState.UserLoggedOut
            } else {
                _startState.value = AppStartState.UserLoggedIn
            }
        }
    }
}