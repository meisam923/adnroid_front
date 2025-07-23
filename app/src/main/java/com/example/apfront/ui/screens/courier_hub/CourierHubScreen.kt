package com.example.apfront.ui.screens.courier_hub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.ui.screens.orderhistory.OrderHistoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierHubScreen(
    navController: NavController,
    viewModel: CourierHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Available Deliveries") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else if (uiState.availableDeliveries.isEmpty()) {
                Text("No available deliveries right now.")
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.availableDeliveries) { order ->
                        OrderHistoryCard(
                            order = order,
                            onClick = {
                                navController.navigate("order_detail/${order.id}")
                            },
                            onRateClick = {}
                        )
                    }
                }
            }
        }
    }
}