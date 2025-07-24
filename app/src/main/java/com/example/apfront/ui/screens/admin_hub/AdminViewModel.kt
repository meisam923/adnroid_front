package com.example.apfront.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.api.*
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.data.repository.AdminRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _users = MutableStateFlow<Resource<List<AdminUserDto>>>(Resource.Loading())
    val users = _users.asStateFlow()

    private val _orders = MutableStateFlow<Resource<List<AdminOrderDto>>>(Resource.Loading())
    val orders = _orders.asStateFlow()

    private val _transactions = MutableStateFlow<Resource<List<TransactionDto>>>(Resource.Loading())
    val transactions = _transactions.asStateFlow()

    private val _coupons = MutableStateFlow<Resource<List<CouponDto>>>(Resource.Loading())
    val coupons = _coupons.asStateFlow()

    private val _restaurants = MutableStateFlow<Resource<List<RestaurantDto>>>(Resource.Loading())
    val restaurants = _restaurants.asStateFlow()

    val userSearchQuery = MutableStateFlow("")
    val orderSearchQuery = MutableStateFlow("")
    val transactionSearchQuery = MutableStateFlow("")
    val restaurantSearchQuery = MutableStateFlow("")

    init {
        fetchAllData()

        viewModelScope.launch {
            userSearchQuery.debounce(500).collect { fetchUsers(it) }
        }
        viewModelScope.launch {
            orderSearchQuery.debounce(500).collect { fetchOrders(it) }
        }
        viewModelScope.launch {
            transactionSearchQuery.debounce(500).collect { fetchTransactions(it) }
        }
        viewModelScope.launch {
            restaurantSearchQuery.debounce(500).collect { fetchRestaurants(it) }
        }
    }

    private fun fetchAllData() {
        fetchUsers()
        fetchOrders()
        fetchTransactions()
        fetchCoupons()
        fetchRestaurants()
    }

    fun fetchUsers(search: String? = null) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        _users.value = Resource.Loading()
        _users.value = adminRepository.getAllUsers(token)
    }

    fun updateUserStatus(userId: Long, status: String) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        adminRepository.updateUserStatus(token, userId, status)
        fetchUsers()
    }

    fun fetchOrders(search: String? = null) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        _orders.value = Resource.Loading()
        _orders.value = adminRepository.getAllOrders(token, search)
    }

    fun fetchTransactions(search: String? = null) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        _transactions.value = Resource.Loading()
        _transactions.value = adminRepository.getTransactions(token, search)
    }

    fun fetchCoupons() = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        _coupons.value = Resource.Loading()
        _coupons.value = adminRepository.getCoupons(token)
    }

    fun fetchRestaurants(search: String? = null) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        _restaurants.value = Resource.Loading()
        _restaurants.value = adminRepository.getAllRestaurantsForAdmin(token, search)
    }

    fun updateRestaurantStatus(restaurantId: Long, status: String) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        adminRepository.updateRestaurantStatus(token, restaurantId, status)
        fetchRestaurants()
    }

    fun deleteCoupon(id: String) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        adminRepository.deleteCoupon(token, id)
        fetchCoupons()
    }
}
