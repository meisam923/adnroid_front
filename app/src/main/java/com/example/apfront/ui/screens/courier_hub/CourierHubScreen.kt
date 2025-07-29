package com.example.apfront.ui.screens.courier_hub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.data.remote.dto.OrderResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierHubScreen(
    navController: NavController,
    viewModel: CourierHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.courier_hub_title)) }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                is CourierHubState.Loading -> CircularProgressIndicator()
                is CourierHubState.NotVerified -> NotVerifiedInfo()
                is CourierHubState.Error -> Text(
                    text = "${stringResource(R.string.error_prefix)} ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                is CourierHubState.Success -> {
                    if (state.deliveries.isEmpty()) {
                        Text(stringResource(R.string.no_available_deliveries))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.deliveries) { order ->
                                DeliveryRequestCard(order = order, onClick = {
                                    navController.navigate("order_detail/${order.id}")
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotVerifiedInfo() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.HourglassEmpty,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.courier_not_verified_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp));
        Text(
            text = stringResource(R.string.courier_not_verified_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        );
    }
}

@Composable
fun DeliveryRequestCard(order: OrderResponse, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Delivery Request #${order.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pickup From: [Restaurant Name]", style = MaterialTheme.typography.bodyMedium)
            Text("Deliver To: ${order.deliveryAddress}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Delivery Fee: $${"%.2f".format(order.courierFee)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}