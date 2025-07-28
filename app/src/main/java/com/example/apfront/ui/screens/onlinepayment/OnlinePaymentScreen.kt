package com.example.apfront.ui.screens.onlinepayment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlinePaymentScreen(
    navController: NavController,
    orderId: Long,
    viewModel: OnlinePaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.paymentState) {
        if (uiState.paymentState is com.example.apfront.util.Resource.Success) {
            // On successful payment, navigate to the order success screen
            navController.navigate("order_success/$orderId") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Complete Payment") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Enter Card Details", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = "4242 4242 4242 4242", onValueChange = {}, label = { Text("Card Number") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = "12/28", onValueChange = {}, label = { Text("MM/YY") }, readOnly = true, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(value = "123", onValueChange = {}, label = { Text("CVC") }, readOnly = true, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.onConfirmPayment() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !uiState.isProcessing
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Pay Now")
                }
            }
        }
    }
}