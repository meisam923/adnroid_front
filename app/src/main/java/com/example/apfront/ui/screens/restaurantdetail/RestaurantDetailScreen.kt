package com.example.apfront.ui.screens.restaurantdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apfront.R
import com.example.apfront.data.remote.dto.FoodItemDto
import com.example.apfront.ui.screens.restaurant_dashboard.Base64Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    navController: NavController,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartTotal = uiState.cart.sumOf { it.item.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.restaurantDetails?.vendor?.name ?: stringResource(R.string.restaurant_detail_loading)) },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.toggle_favorite_description),
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
                            text = stringResource(R.string.cart_items_count, uiState.cart.sumOf { it.quantity }),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { navController.navigate("checkout") }) {
                            Text(text = stringResource(R.string.view_order_button, cartTotal))
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
                                onRemoveItem = { viewModel.onRemoveItem(foodItem) },
                                // --- THIS IS THE FIX ---
                                // Pass the navigation logic to the onClick parameter
                                onClick = { navController.navigate("item_detail/${foodItem.id}") }
                            )
                            HorizontalDivider()
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
    onRemoveItem: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        /*AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("data:image/jpeg;base64," + item.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_placeholder_food),
            error = painterResource(R.drawable.ic_placeholder_food),
            contentDescription = stringResource(R.string.food_item_image_description, item.name),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
*/
        Base64Image(
            base64Data = item.imageUrl,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
            contentDescription = stringResource(R.string.food_item_image_description, item.name),
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "$${"%.2f".format(item.price)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (quantityInCart > 0) {
                IconButton(onClick = onRemoveItem) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = stringResource(R.string.remove_from_cart_button))
                }
                Text(text = "$quantityInCart", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 8.dp))
            }
            IconButton(onClick = onAddItem) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_to_cart_button))
            }
        }
    }
}