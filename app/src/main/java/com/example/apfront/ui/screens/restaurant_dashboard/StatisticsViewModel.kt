package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.api.CouponDto
import com.example.apfront.data.remote.dto.IncomeStatistics
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) :ViewModel(){
    private val _statistics = MutableStateFlow<Resource<List<IncomeStatistics>>>(Resource.Loading())
    val statistics = _statistics.asStateFlow()

    fun fetchStatistics(restaurantId :Int) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        _statistics.value = Resource.Loading()
        _statistics.value = repository.getRestaurantStatistics(token,restaurantId)
    }
}