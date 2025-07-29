package com.example.apfront.ui.screens.onlinepayment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R

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
            navController.navigate("order_success/$orderId") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.online_payment_title)) }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.card_details_label), style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(value = "4242 4242 4242 4242", onValueChange = {}, label = { Text(stringResource(R.string.card_number_label)) }, leadingIcon = { Icon(Icons.Default.CreditCard, null) }, readOnly = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = "12/28", onValueChange = {}, label = { Text(stringResource(R.string.card_expiry_label)) }, readOnly = true, modifier = Modifier.weight(1f))
                OutlinedTextField(value = "123", onValueChange = {}, label = { Text(stringResource(R.string.card_cvc_label)) }, readOnly = true, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.onConfirmPayment() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !uiState.isProcessing,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.pay_now_button))
                }
            }
        }
    }
}