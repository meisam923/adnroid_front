package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.io.encoding.Base64

@HiltViewModel
class CreateRestaurantViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _createState = MutableStateFlow<Resource<RestaurantDto>>(Resource.Idle())
    val createState: StateFlow<Resource<RestaurantDto>> = _createState

    fun createRestaurant(
        token: String,
        name: String,
        address: String,
        phone: String,
        taxFee :Int,
        additionalFee :Int,
        logoBase64: String?
    ) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) {
            _createState.value = Resource.Error("error_401_unauthorized")
            return
        }
        viewModelScope.launch {
            _createState.value = Resource.Loading()
            val request = CreateRestaurantRequest(
                name = name,
                address = address,
                phone = phone,
                logoBase64 = logoBase64,
                taxFee = taxFee,
                additionalFee = additionalFee
            )
            val result = repository.createRestaurant(token, request)
            _createState.value = result
        }
    }
}