package com.example.apfront.ui.screens.orderdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.data.remote.dto.OrderResponse
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.order_detail_title, uiState.order?.id ?: 0)) }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else if (uiState.order != null) {
                OrderDetailContent(order = uiState.order!!, navController = navController)
            }
        }
    }
}

@Composable
fun OrderDetailContent(order: OrderResponse, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.status_label), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = order.status.replace("_", " ").replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.delivery_address_label), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(order.deliveryAddress, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.price_summary_label), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    PriceRow(stringResource(R.string.price_summary_subtotal), order.rawPrice)
                    PriceRow(stringResource(R.string.price_summary_tax), order.taxFee)
                    PriceRow(stringResource(R.string.price_summary_delivery), order.courierFee)
                    PriceRow(stringResource(R.string.price_summary_additional), order.additionalFee)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    PriceRow(stringResource(R.string.price_summary_total), order.payPrice, isTotal = true)
                }
            }
        }

        item {
            if (order.status.equals("COMPLETED", ignoreCase = true)) {
                Button(
                    onClick = {
                        if (order.reviewId != null) {
                            navController.navigate("edit_rating/${order.reviewId}")
                        } else {
                            navController.navigate("submit_rating/${order.id}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (order.reviewId != null) stringResource(R.string.view_edit_review_button) else stringResource(R.string.rate_this_order_button))
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: BigDecimal, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "$${"%.2f".format(value)}",
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
        )
    }
}