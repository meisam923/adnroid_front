package com.example.apfront.ui.screens.restaurantdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.data.remote.dto.FoodItemDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(uiState.restaurantDetails?.vendor?.name ?: "Loading...") })
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
            } else if (uiState.restaurantDetails != null) {
                val details = uiState.restaurantDetails!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Display each menu and its items
                    details.menuTitles.forEach { menuTitle ->
                        // Menu Title Header
                        item {
                            Text(
                                text = menuTitle,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        // List of items for this menu
                        items(details.menus[menuTitle] ?: emptyList()) { foodItem ->
                            FoodItemRow(item = foodItem)
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemRow(item: FoodItemDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "$${item.price}", style = MaterialTheme.typography.bodyLarge)
    }
}