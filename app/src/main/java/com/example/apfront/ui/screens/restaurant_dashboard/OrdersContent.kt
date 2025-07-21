package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.R
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.apfront.data.remote.dto.OrderDto

@Composable
fun OrdersContent(
    restaurantId: Int,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface

    val orderStatuses = listOf("BASE", "ACCEPTED", "REJECTED", "SERVED")

    val tabTitles = listOf(
        stringResource(R.string.order_status_new),
        stringResource(R.string.order_status_accepted),
        stringResource(R.string.order_status_rejected),
        stringResource(R.string.order_status_served)
    )

    val uiState by viewModel.uiState.collectAsState()

    // 🟡 Update orders whenever status or search changes (debounced)
    LaunchedEffect(selectedTabIndex, searchQuery) {
        viewModel.loadOrders(
            restaurantId = restaurantId,
            status = orderStatuses[selectedTabIndex],
            searchQuery = if (searchQuery.isBlank()) null else searchQuery.trim()
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔵 Tabs
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = textColor) }
                )
            }
        }

        // 🔵 Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search orders...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = textColor,
                focusedTextColor = textColor
            ),shape = RoundedCornerShape(16.dp)
        )

        // 🔵 Orders List
        when (val state = uiState) {
            is OrdersUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is OrdersUiState.Success -> {
                if (state.orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No orders in this category.", color = textColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.orders) { order ->
                            OrderCard(
                                order = order,
                                onAccept = {
                                    viewModel.updateOrderStatus(
                                        order.id,
                                        "ACCEPTED",
                                        orderStatuses[selectedTabIndex]
                                    )
                                },
                                onReject = {
                                    viewModel.updateOrderStatus(
                                        order.id,
                                        "REJECTED",
                                        orderStatuses[selectedTabIndex]
                                    )
                                },
                                onServe = {
                                    viewModel.updateOrderStatus(
                                        order.id,
                                        "SERVED",
                                        orderStatuses[selectedTabIndex]
                                    )
                                }
                            )
                        }
                    }
                }
            }

            is OrdersUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("An error occurred. Code: ${state.code}", color = textColor)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderCard(
    order: OrderDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onServe: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val cardColor = when (order.restaurantStatus.uppercase()) {
        "BASE" -> if (isDark) Color(0xFF2D2D2D) else Color(0xFFF5F5F5)
        "ACCEPTED" -> if (isDark) Color(0xFF1E3A5F) else Color(0xFFE3F2FD)
        "REJECTED" -> if (isDark) Color(0xFF4B1C1C) else Color(0xFFFFEBEE)
        "SERVED" -> if (isDark) Color(0xFF1C3D2E) else Color(0xFFE8F5E9)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium, color = textColor)
            Text("Total Price: ${order.payPrice} $", style = MaterialTheme.typography.bodyMedium, color = textColor)

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Items
                    Column {
                        Text("🧾 Items:", style = MaterialTheme.typography.titleSmall, color = textColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.name} × ${item.quantity}", color = textColor)
                                Text("${item.pricePerItem} $", color = textColor)
                            }
                        }
                    }

                    // Price Breakdown
                    Column {
                        Text("💰 Price Details:", style = MaterialTheme.typography.titleSmall, color = textColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Raw Price: ${order.rawPrice} $", color = textColor)
                        Text("Tax: ${order.taxFee} $", color = textColor)
                        Text("Additional Fee: ${order.additionalFee} $", color = textColor)
                        Text("Courier Fee: ${order.courierFee} $", color = textColor)
                        Text("Total Pay: ${order.payPrice} $", style = MaterialTheme.typography.bodyMedium, color = textColor)
                    }

                    // Address
                    Column {
                        Text("📍 Delivery Address:", style = MaterialTheme.typography.titleSmall, color = textColor)
                        Text(order.deliveryAddress, color = textColor)
                    }

                    // Review (only if SERVED)
                    if (order.restaurantStatus.equals("SERVED", ignoreCase = true)) {
                        order.review?.let { review ->
                            Column {
                                Text("⭐ Customer Review:", style = MaterialTheme.typography.titleSmall, color = textColor)
                                review.rating?.let { Text("Rating: $it / 5", color = textColor) }
                                review.comment?.let { Text("Comment: $it", color = textColor) }

                                val imageList = review.base64Images ?: emptyList()
                                if (imageList.isNotEmpty()) {
                                    val pagerState = rememberPagerState(pageCount = { imageList.size })
                                    // HorizontalPager no longer needs pageCount directly if provided in state
                                    HorizontalPager(state = pagerState) { page ->
                                        Base64Image(
                                            base64Data = imageList[page],
                                            contentDescription = "Review image $page",
                                            modifier = Modifier
                                                .padding(vertical = 4.dp)
                                                .height(200.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons
            if (order.restaurantStatus.equals("BASE", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onAccept) {
                        Text("Accept")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = onReject) {
                        Text("Reject")
                    }
                }
            }

            if (order.restaurantStatus.equals("ACCEPTED", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onServe,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Mark as Served", color = Color.White)
                    }
                }
            }
        }
    }}