package com.example.apfront.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
        const val REFRESH_TOKEN = "refresh_token" // Add this
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    // Add these two functions to handle the refresh token
    fun saveRefreshToken(token: String) {
        val editor = prefs.edit()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN, null)
    }
}
