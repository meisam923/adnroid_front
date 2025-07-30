package com.example.apfront.ui.screens.vendorlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apfront.R
import com.example.apfront.data.remote.dto.VendorRestaurantDto
import com.example.apfront.ui.components.PullToRefreshLayout
import com.example.apfront.ui.screens.restaurant_dashboard.Base64Image

@Composable
fun VendorListScreen(
    navController: NavController,
    viewModel: VendorListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        PullToRefreshLayout(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadVendors() },
            modifier = Modifier.padding(paddingValues)
        ) {
            Column {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    label = { Text(stringResource(R.string.search_by_name_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true
                )

                val ratingFilters = listOf(4.5, 4.0, 3.5)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ratingFilters) { rating ->
                        FilterChip(
                            selected = uiState.selectedRating == rating,
                            onClick = { viewModel.onRatingSelected(rating) },
                            label = { Text(stringResource(R.string.rating_plus, rating)) },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                if (uiState.restaurants.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No restaurants found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.restaurants) { restaurant ->
                            RestaurantCard(
                                restaurant = restaurant,
                                onClick = {
                                    navController.navigate("restaurant_detail/${restaurant.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(restaurant: VendorRestaurantDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            /*AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(restaurant.logoUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(R.drawable.ic_placeholder),
                contentDescription = stringResource(R.string.restaurant_card_image_description, restaurant.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )*/
            Base64Image(
                base64Data = restaurant.logoUrl,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentDescription = stringResource(R.string.restaurant_card_image_description, restaurant.name),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = restaurant.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                restaurant.category?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary) }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = restaurant.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (restaurant.isOpen) stringResource(R.string.status_open) else stringResource(R.string.status_closed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (restaurant.isOpen) Color(0xFF388E3C) else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}