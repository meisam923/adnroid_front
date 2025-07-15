package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.apfront.data.remote.dto.RestaurantDto

@Composable
fun RestaurantInfoContent(restaurant: RestaurantDto) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Restaurant Info Form will be here.")
    }
}