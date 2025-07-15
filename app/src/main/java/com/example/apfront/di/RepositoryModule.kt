package com.example.apfront.di

import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.data.repository.AuthRepositoryImpl // FIX: Changed to 'Impl'
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.data.repository.RestaurantRepositoryImpl // FIX: Changed to 'Impl'
import com.example.apfront.data.repository.VendorRepository
import com.example.apfront.data.repository.VendorRepositoryImp // Assuming this file is named VendorRepositoryImp.kt
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        // FIX: Use the correct class name here
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        // FIX: Use the correct class name here
        restaurantRepositoryImpl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Binds
    @Singleton
    abstract fun bindVendorRepository(
        vendorRepositoryImp: VendorRepositoryImp
    ): VendorRepository
}
