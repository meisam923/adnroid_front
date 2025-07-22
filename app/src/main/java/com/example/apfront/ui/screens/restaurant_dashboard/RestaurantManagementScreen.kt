package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantManagementScreen(restaurant: RestaurantDto,
navController : NavController
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Orders", "Menu & Items", "Restaurant Info")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Row(verticalAlignment = Alignment.CenterVertically) {
                    // --- THIS IS THE NEW IMAGE COMPOSABLE ---
                    Base64Image(base64Data =restaurant.logoBase64,
                        contentDescription = "Restaurant Logo",
                        modifier = Modifier
                            .size(40.dp) // Set a size for the icon
                            .clip(CircleShape), // Make it circular
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Add space between image and text

                    Text(restaurant.name)
                } },
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
                1 -> MenuItemsContent(restaurantId = restaurant.id, navController = navController, viewModel = hiltViewModel())
                2 -> RestaurantInfoContent(restaurant = restaurant)
            }
        }
    }
}