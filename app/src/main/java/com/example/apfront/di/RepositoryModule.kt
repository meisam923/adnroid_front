package com.example.apfront.di

import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.data.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.apfront.data.repository.*

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // Add this function inside your RepositoryModule abstract class
    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        restaurantRepositoryImpl: RestaurantRepositoryImpl
    ): RestaurantRepository
}