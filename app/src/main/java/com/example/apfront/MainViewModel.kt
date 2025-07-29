package com.example.apfront

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.repository.NotificationRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// FIX 1: Update AppStartState to hold the user's role upon login.
sealed interface AppStartState {
    object Loading : AppStartState
    data class UserLoggedIn(val role: String) : AppStartState // Changed from object to data class
    object UserLoggedOut : AppStartState
}

data class MainUiState(
    val unreadNotificationCount: Int = 0
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository // Inject the new repository
) : ViewModel() {

    private val _startState = MutableStateFlow<AppStartState>(AppStartState.Loading)
    val startState: StateFlow<AppStartState> = _startState

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken()
            val role = sessionManager.getUserRole()
            if (token.isNullOrEmpty() || role.isNullOrEmpty()) {
                _startState.value = AppStartState.UserLoggedOut
            } else {
                _startState.value = AppStartState.UserLoggedIn(role)
                // If the user is logged in, fetch their notification count
                fetchNotificationCount()
            }
        }
    }

    fun fetchNotificationCount() {
        viewModelScope.launch {
            val token = sessionManager.getAuthToken() ?: return@launch
            val result = notificationRepository.getNotifications(token)
            if (result is Resource.Success) {
                _uiState.update { it.copy(unreadNotificationCount = result.data?.size ?: 0) }
            }
        }
    }
    fun onNotificationsViewed() {
        _uiState.update { it.copy(unreadNotificationCount = 0) }
    }
}