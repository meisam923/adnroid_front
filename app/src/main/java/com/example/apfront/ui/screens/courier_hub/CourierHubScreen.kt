package com.example.apfront.ui.screens.courier_hub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.data.remote.dto.OrderResponse

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
                        // We create a new, more appropriate card for couriers
                        DeliveryRequestCard(order = order, onClick = {
                            navController.navigate("order_detail/${order.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryRequestCard(order: OrderResponse, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Delivery Request #${order.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pickup From: [Restaurant Name]", style = MaterialTheme.typography.bodyMedium) // TODO: Add restaurant name to OrderResponse DTO
            Text("Deliver To: ${order.deliveryAddress}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Delivery Fee: $${"%.2f".format(order.courierFee)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}