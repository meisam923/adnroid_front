package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.apfront.data.remote.dto.RestaurantDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantManagementScreen(restaurant: RestaurantDto) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Orders", "Menu & Items", "Restaurant Info")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant.name) },
                actions = {
                    AssistChip(
                        onClick = { /* No action needed */ },
                        label = { Text(restaurant.approvalStatus.uppercase()) }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> OrdersContent(restaurantId = restaurant.id)
                1 -> MenuItemsContent(restaurantId = restaurant.id)
                2 -> RestaurantInfoContent(restaurant = restaurant)
            }
        }
    }
}