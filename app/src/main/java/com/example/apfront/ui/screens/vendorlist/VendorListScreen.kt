package com.example.apfront.ui.screens.vendorlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R // Import your app's R class
import com.example.apfront.data.remote.dto.VendorRestaurantDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorListScreen(
    navController: NavController,
    viewModel: VendorListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurants") },
                actions = {
                    // --- FIX: The onClick now handles both logic and navigation ---
                    IconButton(onClick = {
                        // 1. Tell the ViewModel to clear the session data
                        viewModel.logout()
                        // 2. Navigate back to the login screen and clear the history
                        navController.navigate("login") {
                            // This removes all screens from the back stack, so the user
                            // can't press the back button to get back to this screen.
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                    // --- END OF FIX ---
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.restaurants) { restaurant ->
                        RestaurantCard(restaurant = restaurant)
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(restaurant: VendorRestaurantDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = restaurant.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = restaurant.address, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                // FIX: Use string resources for rating label and status
                val ratingText = restaurant.rating?.toString() ?: stringResource(id = R.string.rating_not_available)
                Text(
                    text = "${stringResource(id = R.string.rating_label)} $ratingText",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))

                val statusText = if (restaurant.isOpen) {
                    stringResource(id = R.string.status_open)
                } else {
                    stringResource(id = R.string.status_closed)
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (restaurant.isOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}