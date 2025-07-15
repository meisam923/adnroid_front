package com.example.apfront.ui.screens.seller_hub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.ui.screens.restaurant_dashboard.CreateRestaurantScreen
import com.example.apfront.ui.screens.restaurant_dashboard.RestaurantManagementScreen

@Composable
fun SellerHubScreen(
    navController: NavController,
    viewModel: SellerHubViewModel = hiltViewModel()
) {
    // IMPORTANT: Get the real token you saved after login

    // This effect runs only once when the screen is first displayed
    LaunchedEffect(key1 = Unit) {
        viewModel.checkRestaurantStatus()
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is SellerHubUiState.Loading -> {
                CircularProgressIndicator()
            }
            is SellerHubUiState.NoRestaurantFound -> {
                // If no restaurant exists, show the creation screen
                CreateRestaurantScreen()
            }
            is SellerHubUiState.Success -> {
                // If a restaurant exists, check its status and show the correct screen
                when (state.restaurant.approvalStatus.uppercase()) {
                    "REGISTERED" -> RestaurantManagementScreen(restaurant = state.restaurant)
                    "WAITING" -> WaitingForApprovalScreen(
                        onGoBackPressed = { navController.popBackStack() } // Allows user to go back
                    )
                    "REJECTED" -> RestaurantRejectedScreen (
                        onGoBackPressed = { navController.popBackStack() }
                    )
                    // You can add a screen for "SUSPENDED" here as well
                    else -> Text("Error: Unknown Restaurant Status '${state.restaurant.approvalStatus}'")
                }
            }
            is SellerHubUiState.Error -> {
                // Here you would use your translated error message logic
                Text("An error occurred: ${state.message}")
            }
        }
    }
}