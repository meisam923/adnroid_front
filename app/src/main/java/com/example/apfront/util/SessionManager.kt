package com.example.apfront.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_ROLE = "user_role"
    }

    fun saveSession(token: String?, refreshToken: String?, role: String?) {
        prefs.edit {
            if (token != null) {
                putString(AUTH_TOKEN, token)
            }
            if (refreshToken != null) {
                putString(REFRESH_TOKEN, refreshToken)
            }
            if (role != null) {
                putString(USER_ROLE, role)
            }
        }
    }

    fun getAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }

    fun getUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}