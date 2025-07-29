package com.example.apfront.ui.screens.restaurant_dashboard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.CreateRestaurantRequest
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import com.example.apfront.util.uriToBase64 // We'll create this helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantInfoViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val name = MutableStateFlow("")
    val address = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val taxFee = MutableStateFlow("")
    val additionalFee = MutableStateFlow("")
    val logoImageUri = MutableStateFlow<Uri?>(null) // For newly selected images
    val existingLogoBase64 = MutableStateFlow<String?>(null) // For the logo from the server

    private val _updateState = MutableStateFlow<Resource<RestaurantDto>>(Resource.Idle())
    val updateState: StateFlow<Resource<RestaurantDto>> = _updateState

    fun loadRestaurantInfo(restaurant: RestaurantDto) {
        name.value = restaurant.name
        address.value = restaurant.address
        phone.value = restaurant.phone
        taxFee.value = restaurant.taxFee.toString()
        additionalFee.value = restaurant.additionalFee.toString()
        existingLogoBase64.value = restaurant.logoBase64
        logoImageUri.value = null // Reset any newly selected image
    }

    fun onUpdateClicked(restaurantId: Int, context: Context) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) {  return }

        viewModelScope.launch {
            _updateState.value = Resource.Loading()

            val newLogoBase64 = logoImageUri.value?.let { uriToBase64(context, it) }

            val request = CreateRestaurantRequest(
                name = name.value,
                address = address.value,
                phone = phone.value,
                logoBase64 = newLogoBase64 ?: existingLogoBase64.value,
                taxFee = taxFee.value.toIntOrNull() ?: 0,
                additionalFee = additionalFee.value.toIntOrNull() ?: 0
            )
            val result = repository.updateRestaurant(token, restaurantId, request)
            _updateState.value = result
        }
    }
}