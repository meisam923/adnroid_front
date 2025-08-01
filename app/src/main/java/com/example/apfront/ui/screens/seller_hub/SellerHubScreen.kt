package com.example.apfront.ui.screens.seller_hub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.ui.screens.restaurant_dashboard.CreateRestaurantScreen
import com.example.apfront.ui.screens.restaurant_dashboard.RestaurantManagementScreen

@Composable
fun SellerHubScreen(
    navController: NavController,
    viewModel: SellerHubViewModel = hiltViewModel()
) {

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
                CreateRestaurantScreen(onSuccess = {
                    viewModel.checkRestaurantStatus()
                })
            }
            is SellerHubUiState.Success -> {
                when (state.restaurant.approvalStatus.uppercase()) {
                    "REGISTERED" -> RestaurantManagementScreen(restaurant = state.restaurant, navController = navController)
                    "WAITING" -> WaitingForApprovalScreen(
                        onGoBackPressed = { navController.popBackStack() } // Allows user to go back
                    )
                    "REJECTED" -> RestaurantRejectedScreen (
                        onGoBackPressed = { navController.popBackStack() }
                    )
                    else -> Text("Error: Unknown Restaurant Status '${state.restaurant.approvalStatus}'")
                }
            }
            is SellerHubUiState.Error -> {
                val errorMessage = when (state.message) {
                    "error_400_invalid_input" -> stringResource(com.example.apfront.R.string.error_400_invalid_input)
                    "error_401_unauthorized" -> stringResource(com.example.apfront.R.string.error_401_unauthorized)
                    "error_403_forbidden" -> stringResource(com.example.apfront.R.string.error_403_forbidden)
                    "error_404_not_found" -> stringResource(com.example.apfront.R.string.error_404_not_found)
                    "error_409_conflict" -> stringResource(com.example.apfront.R.string.error_409_conflict)
                    "error_500_server_error" -> stringResource(com.example.apfront.R.string.error_500_server_error)
                    "error_network_connection" -> stringResource(com.example.apfront.R.string.error_network_connection)
                    else -> stringResource(R.string.error_unknown)
                }

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )            }
        }
    }
}