package com.example.apfront.ui.screens.orderhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.data.remote.dto.OrderResponse
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.order_history_title)) }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("${stringResource(R.string.error_prefix)} ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else if (uiState.orders.isEmpty()) {
                Text(stringResource(R.string.no_orders_message))
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.orders) { order ->
                        OrderHistoryCard(
                            order = order,
                            onClick = { navController.navigate("order_detail/${order.id}") },

                            onRateClick = {
                                if (order.reviewId != null) {
                                    navController.navigate("edit_rating/${order.reviewId}")
                                } else {
                                    navController.navigate("submit_rating/${order.id}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: OrderResponse, onClick: () -> Unit, onRateClick: () -> Unit) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.order_card_title, order.id), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.order_card_status, order.status), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.order_card_total, order.payPrice), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.order_card_placed_on, order.createdAt.format(formatter)), style = MaterialTheme.typography.bodySmall)

            if (order.status.equals("COMPLETED", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRateClick, modifier = Modifier.fillMaxWidth()) {
                    Text(if (order.reviewId != null) stringResource(R.string.view_edit_review_button) else stringResource(R.string.rate_this_order_button))
                }
            }
        }
    }
}