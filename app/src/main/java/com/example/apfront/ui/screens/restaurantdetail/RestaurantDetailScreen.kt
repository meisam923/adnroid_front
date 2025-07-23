package com.example.apfront.ui.screens.restaurantdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.data.remote.dto.FoodItemDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    navController: NavController,
    viewModel: RestaurantDetailViewModel = hiltViewModel() // Hilt automatically provides the ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartTotal = uiState.cart.sumOf { it.item.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.restaurantDetails?.vendor?.name ?: "Loading...") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.cart.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.cart.sumOf { it.quantity }} items",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { navController.navigate("checkout") }) {
                            Text(text = "View Order - $${"%.2f".format(cartTotal)}")
                        }
                    }
                }
            }
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
                    details.menuTitles.forEach { menuTitle ->
                        item {
                            Text(
                                text = menuTitle,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        val itemsForMenu = details.menus?.get(menuTitle) ?: emptyList()
                        items(itemsForMenu) { foodItem ->
                            FoodItemRow(
                                item = foodItem,
                                quantityInCart = uiState.cart.find { it.item.id == foodItem.id }?.quantity ?: 0,
                                onAddItem = { viewModel.onAddItem(foodItem) },
                                onRemoveItem = { viewModel.onRemoveItem(foodItem) }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemRow(
    item: FoodItemDto,
    quantityInCart: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "$${item.price}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (quantityInCart > 0) {
                IconButton(onClick = onRemoveItem) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Remove one")
                }
                Text(text = "$quantityInCart", modifier = Modifier.padding(horizontal = 8.dp))
            }
            IconButton(onClick = onAddItem) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add one")
            }
        }
    }
}