package com.example.apfront.data.remote

import com.example.apfront.data.remote.api.TokenRefreshApiService
import com.example.apfront.data.remote.dto.RefreshTokenRequest
import com.example.apfront.util.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val tokenRefreshApiService: TokenRefreshApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        println("AuthAuthenticator: Intercepted a 401 Unauthorized error. Attempting to refresh token.")

        val refreshToken = runBlocking {
            sessionManager.getRefreshToken()
        }
        println("AuthAuthenticator: Current refresh token is: $refreshToken")

        if (refreshToken.isNullOrBlank()) {
            println("AuthAuthenticator: No refresh token found. Logging out and giving up.")
            runBlocking { sessionManager.clearSession() }
            return null
        }

        return runBlocking {
            println("AuthAuthenticator: Making API call to /auth/refresh-token...")
            val refreshResponse = tokenRefreshApiService.refreshToken(RefreshTokenRequest(refreshToken))

            if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!
                println("AuthAuthenticator: Token refresh SUCCESSFUL.")
                println("AuthAuthenticator: New Access Token: ${newTokens.accessToken}")

                sessionManager.saveSession(
                    token = newTokens.accessToken,
                    refreshToken = newTokens.refreshToken,
                    role = newTokens.user.role
                )

                println("AuthAuthenticator: Retrying original request.")
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                println("AuthAuthenticator: Token refresh FAILED. Response code: ${refreshResponse.code()}")
                println("AuthAuthenticator: Error body: ${refreshResponse.errorBody()?.string()}")

                sessionManager.clearSession()
                null
            }
        }
    }
}