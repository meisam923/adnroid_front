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
    fun provideTokenRefreshApiService(): TokenRefreshApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authenticator: AuthAuthenticator): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .authenticator(authenticator)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // --- All of your API service providers are correct ---
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideVendorApiService(retrofit: Retrofit): VendorApiService = retrofit.create(VendorApiService::class.java)

    @Provides
    @Singleton
    fun provideItemApiService(retrofit: Retrofit): ItemApiService = retrofit.create(ItemApiService::class.java)

    @Provides
    @Singleton
    fun provideOrderApiService(retrofit: Retrofit): OrderApiService = retrofit.create(OrderApiService::class.java)

    @Provides
    @Singleton
    fun provideCouponApiService(retrofit: Retrofit): CouponApiService = retrofit.create(CouponApiService::class.java)

    @Provides
    @Singleton
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService = retrofit.create(PaymentApiService::class.java)

    @Provides
    @Singleton
    fun provideFavoriteApiService(retrofit: Retrofit): FavoriteApiService = retrofit.create(FavoriteApiService::class.java)

    @Provides
    @Singleton
    fun provideCourierApiService(retrofit: Retrofit): CourierApiService = retrofit.create(CourierApiService::class.java)

    @Provides
    @Singleton
    fun provideRatingApiService(retrofit: Retrofit): RatingApiService = retrofit.create(RatingApiService::class.java)

    @Provides
    @Singleton
    fun provideAdminApiService(retrofit: Retrofit): AdminApiService = retrofit.create(AdminApiService::class.java)
    @Provides @Singleton
    fun provideCourierApiService(retrofit: Retrofit): CourierApiService = retrofit.create(CourierApiService::class.java)
    @Provides @Singleton
    fun provideRatingApiService(retrofit: Retrofit): RatingApiService = retrofit.create(RatingApiService::class.java)
    
    @Provides
    @Singleton
    fun provideRestaurantApiService(retrofit: Retrofit): RestaurantApiService = retrofit.create(RestaurantApiService::class.java)

}

