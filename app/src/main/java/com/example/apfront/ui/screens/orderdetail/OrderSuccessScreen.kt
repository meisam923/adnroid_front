package com.example.apfront.ui.screens.orderdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.apfront.ui.screens.orderdetail.OrderSuccessViewModel
import com.example.apfront.ui.screens.orderdetail.PriceRow

@Composable
fun OrderSuccessScreen(
    navController: NavController,
    viewModel: OrderSuccessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else if (uiState.order != null) {
            val order = uiState.order!!
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.order_placed_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.order_success_message, order.id),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.order_summary_label), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.delivering_to_label, order.deliveryAddress), style = MaterialTheme.typography.bodyMedium)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    PriceRow(label = stringResource(R.string.total_paid_label), value = order.payPrice, isTotal = true)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    navController.navigate("home") { popUpTo(0) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_to_home_button))
            }
        } else {
            Text("Error: Could not load order details.", color = MaterialTheme.colorScheme.error)
        }
    }
}