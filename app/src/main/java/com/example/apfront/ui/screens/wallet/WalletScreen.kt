package com.example.apfront.ui.screens.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.data.remote.dto.TransactionDto
import com.example.apfront.ui.components.PullToRefreshLayout
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

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.wallet_title)) }) }) { padding ->
        PullToRefreshLayout(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(stringResource(R.string.current_balance_label), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(
                                text = "$${"%.2f".format(uiState.calculatedBalance)}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                item {
                    Column {
                        Text(stringResource(R.string.top_up_wallet_label), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = topUpAmount,
                                onValueChange = { topUpAmount = it },
                                label = { Text(stringResource(R.string.amount_label)) },
                                leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val amount = topUpAmount.toBigDecimalOrNull()
                                    if (amount != null) {
                                        viewModel.topUp(amount)
                                        topUpAmount = ""
                                    }
                                },
                                modifier = Modifier.height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.add_funds_button))
                            }
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text(stringResource(R.string.transaction_history_label), style = MaterialTheme.typography.titleLarge)
                    }
                }

                if (uiState.transactions.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_transactions_message), textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    items(uiState.transactions) { transaction ->
                        AnimatedVisibility(visible = true, enter = fadeIn(animationSpec = tween(500))) {
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
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a") }
    val isPayment = transaction.type == "PAYMENT"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (transaction.type) {
                        "PAYMENT" -> stringResource(R.string.transaction_type_payment)
                        "TOP_UP" -> stringResource(R.string.transaction_type_top_up)
                        else -> transaction.type.replace("_", " ")
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.createdAt.format(formatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (transaction.method != null) {
                    Text(
                        text = stringResource(R.string.transaction_method_label, transaction.method.replaceFirstChar { it.uppercase() }),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = (if (isPayment) "- " else "+ ") + "$${"%.2f".format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPayment) MaterialTheme.colorScheme.error else Color(0xFF388E3C)
            )
        }
    }
}