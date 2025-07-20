package com.example.apfront.di

import com.example.apfront.data.remote.AuthAuthenticator
import com.example.apfront.data.remote.api.*
import com.example.apfront.util.Constants
import com.example.apfront.util.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. This provides the Authenticator itself.
    @Provides
    @Singleton
    fun provideAuthAuthenticator(
        sessionManager: SessionManager,
        tokenRefreshApiService: TokenRefreshApiService
    ): AuthAuthenticator {
        return AuthAuthenticator(sessionManager, tokenRefreshApiService)
    }

    // 2. This provides the custom OkHttpClient that USES the Authenticator.
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authenticator: AuthAuthenticator
    ): OkHttpClient {
        // 1. Create the logger
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Log everything: headers and body

        // 2. Build the client, now with the logger attached
        return OkHttpClient.Builder()
            .addInterceptor(logging) // Add the logger as an interceptor
            .authenticator(authenticator)
            .build()
    }

    // 3. This is your main Retrofit instance. It now uses the custom OkHttpClient.
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient) // Use the client with the authenticator
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 4. All your normal API services will now automatically use the main Retrofit instance,
    // which means they all get the benefit of the automatic token refresh.
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRestaurantApiService(retrofit: Retrofit): RestaurantApiService {
        return retrofit.create(RestaurantApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideVendorApiService(retrofit: Retrofit): VendorApiService {
        return retrofit.create(VendorApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenRefreshApiService(): TokenRefreshApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshApiService::class.java)
    }
}