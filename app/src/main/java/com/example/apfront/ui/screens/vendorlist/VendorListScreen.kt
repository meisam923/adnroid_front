package com.example.apfront.ui.screens.vendorlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import com.example.apfront.R
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
                title = { Text(stringResource(id = R.string.restaurants_title)) },
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // --- SEARCH AND FILTER UI ---
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search by name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
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
                        label = { Text("$rating+") },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Rating", modifier = Modifier.size(18.dp)) }
                    )
                }
            }
            // --- END OF UI ---

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.error != null) {
                    Text(
                        text = "${stringResource(id = R.string.error_prefix)} ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = restaurant.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = restaurant.address, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                val ratingText = restaurant.rating?.toString() ?: stringResource(id = R.string.rating_not_available)
                Text(
                    text = "${stringResource(id = R.string.rating_label)} $ratingText",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(16.dp))
                val statusText = if (restaurant.isOpen) stringResource(id = R.string.status_open) else stringResource(id = R.string.status_closed)
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (restaurant.isOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}