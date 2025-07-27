package com.example.apfront.ui.screens.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.data.remote.dto.TransactionDto
import com.example.apfront.ui.components.PullToRefreshLayout // Import the new component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var topUpAmount by remember { mutableStateOf("") }

    Scaffold(topBar = { TopAppBar(title = { Text("My Wallet") }) }) { padding ->
        // We wrap the entire screen content in our new PullToRefreshLayout.
        PullToRefreshLayout(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
            // The rest of your UI goes inside the content block.
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Balance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Current Balance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            text = "$${"%.2f".format(uiState.calculatedBalance)}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Top-Up Section
                Text("Top Up Wallet", style = MaterialTheme.typography.titleLarge)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = topUpAmount,
                        onValueChange = { topUpAmount = it },
                        label = { Text("Amount") },
                        leadingIcon = { Text("$") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amount = topUpAmount.toBigDecimalOrNull()
                            if (amount != null) {
                                viewModel.topUp(amount)
                                topUpAmount = "" // Clear the field after submitting
                            }
                        },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Add Funds")
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 24.dp))

                // Transaction History
                Text("Transaction History", style = MaterialTheme.typography.titleLarge)

                // We no longer need a separate loading check here, as the pull-to-refresh
                // indicator handles it.
                if (uiState.transactions.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No transactions yet.", textAlign = TextAlign.Center)
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                        items(uiState.transactions) { transaction ->
                            TransactionRow(transaction)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TransactionRow(transaction: TransactionDto) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.type.replace("_", " "), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(transaction.createdAt.format(formatter), style = MaterialTheme.typography.bodySmall)

            // --- THIS IS THE FIX ---
            // Display the payment method if it exists for this transaction type.
            if (transaction.method != null) {
                Text(
                    text = "Method: ${transaction.method.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // --- END OF FIX ---
        }
        Text(
            text = (if (transaction.type == "PAYMENT") "- " else "+ ") + "$${"%.2f".format(transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (transaction.type == "PAYMENT") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}
