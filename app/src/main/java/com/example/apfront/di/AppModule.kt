package com.example.apfront.di

import com.example.apfront.data.remote.AuthAuthenticator
import com.example.apfront.data.remote.api.*
import com.example.apfront.util.Constants
import com.example.apfront.util.LocalDateTimeAdapter
import com.example.apfront.util.SessionManager
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
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

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authenticator: AuthAuthenticator
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .authenticator(authenticator)
            .connectTimeout(120, TimeUnit.SECONDS) // 2 minutes
            .readTimeout(120, TimeUnit.SECONDS)    // 2 minutes
            .writeTimeout(120, TimeUnit.SECONDS)   // 2 minutes
            .build()
    }
    // 3. This is your main Retrofit instance. It now uses the custom OkHttpClient.
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Create a custom Gson instance that knows about our adapter
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            // Use our custom Gson instance for JSON conversion
            .addConverterFactory(GsonConverterFactory.create(gson))
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

    @Provides
    @Singleton
    fun provideItemApiService(retrofit: Retrofit): ItemApiService {
        return retrofit.create(ItemApiService::class.java)
    }

    @Provides @Singleton
    fun provideCouponApiService(retrofit: Retrofit): CouponApiService = retrofit.create(CouponApiService::class.java)

    @Provides @Singleton
    fun provideOrderApiService(retrofit: Retrofit): OrderApiService = retrofit.create(OrderApiService::class.java)
    @Provides @Singleton
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService = retrofit.create(PaymentApiService::class.java)
    @Provides @Singleton
    fun provideFavoriteApiService(retrofit: Retrofit): FavoriteApiService = retrofit.create(FavoriteApiService::class.java)

    @Provides @Singleton
    fun provideRatingApiService(retrofit: Retrofit): RatingApiService = retrofit.create(RatingApiService::class.java)

    @Provides @Singleton
    fun provideCourierApiService(retrofit: Retrofit): CourierApiService = retrofit.create(CourierApiService::class.java)
}