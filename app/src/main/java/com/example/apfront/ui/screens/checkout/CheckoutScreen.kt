package com.example.apfront.ui.screens.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.data.model.CartItem
import com.example.apfront.data.remote.dto.CouponDto
import com.example.apfront.util.Resource
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val cart by viewModel.cartManager.cart.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val subtotal = cart.sumOf { it.item.price * it.quantity }.toBigDecimal()
    val coupon = (uiState.couponState as? Resource.Success)?.data
    val discount = coupon?.value ?: BigDecimal.ZERO
    val total = (subtotal - discount).coerceAtLeast(BigDecimal.ZERO)

    val hasSufficientBalance = uiState.calculatedBalance >= total
    val isButtonEnabled = uiState.deliveryAddress.isNotBlank() && !uiState.isSubmitting &&
            (uiState.selectedPaymentMethod == "online" || hasSufficientBalance)

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Error") },
            text = { Text(uiState.errorMessage!!) },
            confirmButton = { Button(onClick = { viewModel.dismissError() }) { Text("OK") } }
        )
    }

    LaunchedEffect(uiState.orderSubmissionState) {
        val submissionState = uiState.orderSubmissionState
        if (submissionState is Resource.Success) {
            val orderId = submissionState.data
            if (orderId != null) {
                if (uiState.selectedPaymentMethod == "online") {
                    navController.navigate("online_payment/$orderId")
                } else { // Wallet payment was already successful
                    navController.navigate("order_success/$orderId") {
                        popUpTo("home") { inclusive = true }
                    }
                }
                viewModel.onNavigationHandled()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Your Order") }) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Delivery Address", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = uiState.deliveryAddress,
                        onValueChange = { viewModel.onAddressChanged(it) },
                        label = { Text("Enter your address") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        isError = uiState.errorMessage?.contains("address", ignoreCase = true) == true
                    )

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    Text("Items in Cart", style = MaterialTheme.typography.titleLarge)
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(cart) { cartItem ->
                            CartItemRow(cartItem)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    Text("Payment Method", style = MaterialTheme.typography.titleLarge)
                    val paymentMethods = listOf("online", "wallet")
                    Column(Modifier.padding(top = 8.dp)) {
                        paymentMethods.forEach { method ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (method == uiState.selectedPaymentMethod),
                                        onClick = { viewModel.onPaymentMethodSelected(method) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (method == uiState.selectedPaymentMethod),
                                    onClick = null
                                )
                                Text(
                                    text = method.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                if (method == "wallet") {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "(Balance: $${"%.2f".format(uiState.calculatedBalance)})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (hasSufficientBalance) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    CouponSection(viewModel = viewModel, couponState = uiState.couponState)
                    PriceSummary(subtotal = subtotal.toDouble(), discount = discount, total = total)

                    Button(
                        onClick = { viewModel.onProceedToPayment() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = isButtonEnabled
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Proceed to Payment")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${cartItem.quantity}x", style = MaterialTheme.typography.bodyLarge)
        Text(cartItem.item.name, modifier = Modifier.weight(1f).padding(horizontal = 16.dp))
        Text("$${"%.2f".format(cartItem.item.price * cartItem.quantity)}")
    }
}

@Composable
fun CouponSection(viewModel: CheckoutViewModel, couponState: Resource<CouponDto>) {
    var couponCode by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = couponCode,
                onValueChange = { couponCode = it },
                label = { Text("Coupon Code") },
                modifier = Modifier.weight(1f),
                enabled = couponState !is Resource.Loading
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.applyCoupon(couponCode) },
                enabled = couponState !is Resource.Loading
            ) {
                Text("Apply")
            }
        }
        when (couponState) {
            is Resource.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
            is Resource.Success -> Text("Coupon '${couponState.data?.couponCode}' applied!", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            is Resource.Error -> Text(couponState.message ?: "Invalid coupon", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            else -> {}
        }
    }
}

@Composable
fun PriceSummary(subtotal: Double, discount: BigDecimal, total: BigDecimal) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal")
                Text("$${"%.2f".format(subtotal)}")
            }
            if (discount > BigDecimal.ZERO) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Discount")
                    Text("-$${"%.2f".format(discount)}", color = MaterialTheme.colorScheme.primary)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("$${"%.2f".format(total)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
