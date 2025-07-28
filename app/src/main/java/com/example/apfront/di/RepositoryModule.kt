package com.example.apfront.di

import com.example.apfront.data.repository.AdminRepository
import com.example.apfront.data.repository.AdminRepositoryImpl
import com.example.apfront.data.repository.AuthRepository
import com.example.apfront.data.repository.AuthRepositoryImpl // FIX: Changed to 'Impl'
import com.example.apfront.data.repository.CouponRepository
import com.example.apfront.data.repository.CouponRepositoryImp
import com.example.apfront.data.repository.CourierRepository
import com.example.apfront.data.repository.CourierRepositoryImp
import com.example.apfront.data.repository.FavoriteRepository
import com.example.apfront.data.repository.FavoriteRepositoryImp
import com.example.apfront.data.repository.ItemRepository
import com.example.apfront.data.repository.ItemRepositoryImp
import com.example.apfront.data.repository.OrderRepository
import com.example.apfront.data.repository.OrderRepositoryImp
import com.example.apfront.data.repository.PaymentRepository
import com.example.apfront.data.repository.PaymentRepositoryImp
import com.example.apfront.data.repository.RatingRepository
import com.example.apfront.data.repository.RatingRepositoryImp
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

    @Binds
    @Singleton
    abstract fun bindItemRepository(
        itemRepositoryImp: ItemRepositoryImp
    ): ItemRepository

    @Binds @Singleton
    abstract fun bindCouponRepository(imp: CouponRepositoryImp): CouponRepository

    @Binds @Singleton
    abstract fun bindOrderRepository(imp: OrderRepositoryImp): OrderRepository

    @Binds @Singleton
    abstract fun bindPaymentRepository(imp: PaymentRepositoryImp): PaymentRepository

    @Binds @Singleton
    abstract fun bindFavoriteRepository(imp: FavoriteRepositoryImp): FavoriteRepository
    @Binds @Singleton
    abstract fun bindAdminRepository(imp: AdminRepositoryImpl): AdminRepository
    @Binds @Singleton
    abstract fun bindCourierRepository(imp: CourierRepositoryImp): CourierRepository
    @Binds @Singleton
    abstract fun bindRatingRepository(imp: RatingRepositoryImp): RatingRepository




    @Binds
    @Singleton
    abstract fun bindCourierRepository(
        courierRepositoryImp: CourierRepositoryImp
    ): CourierRepository

    @Binds
    @Singleton
    abstract fun bindRatingRepository(
        ratingRepositoryImp: RatingRepositoryImp
    ): RatingRepository
}