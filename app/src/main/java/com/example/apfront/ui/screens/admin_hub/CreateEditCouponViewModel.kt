package com.example.apfront.ui.screens.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.api.CreateCouponRequest
import com.example.apfront.data.repository.AdminRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface SaveCouponState {
    object Idle : SaveCouponState
    object Loading : SaveCouponState
    object Success : SaveCouponState
    data class Error(val message: String) : SaveCouponState
}

@HiltViewModel
class CreateEditCouponViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Form fields
    val couponCode = MutableStateFlow("")
    val type = MutableStateFlow("percent") // Default value
    val value = MutableStateFlow("")
    val minPrice = MutableStateFlow("")
    val userCount = MutableStateFlow("")
    val startDate = MutableStateFlow<LocalDate?>(null)
    val endDate = MutableStateFlow<LocalDate?>(null)

    private val _saveState = MutableStateFlow<SaveCouponState>(SaveCouponState.Idle)
    val saveState = _saveState.asStateFlow()

    private val couponId: String? = savedStateHandle.get("couponId")

    init {
        // If a couponId is passed, load its data for editing
        couponId?.let { loadCoupon(it) }
    }

    private fun loadCoupon(id: String) = viewModelScope.launch {
        val token = sessionManager.getAuthToken() ?: return@launch
        val coupon = adminRepository.getCoupon(token, id)
        if (coupon is Resource.Success) {
            coupon.data?.let { data ->
                couponCode.value = data.couponCode
                type.value = data.type
                value.value = data.value.toString()
                minPrice.value = data.minPrice.toString()
                userCount.value = data.userCount.toString()
                startDate.value = LocalDate.parse(data.startDate)
                endDate.value = LocalDate.parse(data.endDate)
            }
        }
    }

    fun onStartDateSelected(millis: Long?) {
        millis?.let {
            startDate.value = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    fun onEndDateSelected(millis: Long?) {
        millis?.let {
            endDate.value = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    fun saveCoupon() = viewModelScope.launch {
        if (couponCode.value.isBlank() || value.value.isBlank() || minPrice.value.isBlank() ||
            userCount.value.isBlank() || endDate.value == null || startDate.value == null ||
            startDate.value!!.isAfter(endDate.value)) {

            _saveState.value = SaveCouponState.Error("Check the fields, they cannot be empty or invalid.")
            return@launch
        }

        val token = sessionManager.getAuthToken() ?: return@launch
        _saveState.value = SaveCouponState.Loading

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        val request = CreateCouponRequest(
            couponCode = couponCode.value,
            type = type.value,
            value = value.value.toDoubleOrNull() ?: 0.0,
            minPrice = minPrice.value.toIntOrNull() ?: 0,
            userCount = userCount.value.toIntOrNull() ?: 0,
            startDate = startDate.value?.format(formatter) ?: "",
            endDate = endDate.value?.format(formatter) ?: ""
        )

        val result = if (couponId == null) {
            adminRepository.createCoupon(token, request)
        } else {
            adminRepository.updateCoupon(token, couponId, request)
        }

        when (result) {
            is Resource.Success -> {
                _saveState.value = SaveCouponState.Success
            }
            is Resource.Error -> {
                _saveState.value = SaveCouponState.Error(result.message ?: "Failed to save coupon")
            }
            else -> {
                _saveState.value = SaveCouponState.Error("Unknown error occurred")
            }
        }
    }
}
