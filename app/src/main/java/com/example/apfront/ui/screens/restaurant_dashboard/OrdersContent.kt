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
                                ,
                                reply = { reviewId, reply ->
                                    viewModel.submitReplyReview(reviewId, reply)
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
    onServe: () -> Unit,
    reply: (Long, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val cardColor = when (order.restaurantStatus.uppercase()) {
        "BASE" -> if (isDark) Color(0xFF2D2D2D) else Color(0xFFF5F5F5)
        "ACCEPTED" -> if (isDark) Color(0xFF1E3A5F) else Color(0xFFE3F2FD)
        "REJECTED" -> if (isDark) Color(0xFF4B1C1C) else Color(0xFFFFEBEE)
        "SERVED" -> if (isDark) Color(0xFF618096) else Color(0xFFE8F5E9)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Order #${order.id}", style = MaterialTheme.typography.titleLarge, color = textColor)
                    Text(
                        "Status: ${order.restaurantStatus}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    "${order.payPrice} $",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {

                    // Items
                    SectionHeader("Ordered Items")
                    Spacer(modifier = Modifier.height(6.dp))
                    order.items.forEach {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${it.name} × ${it.quantity}", color = textColor)
                            Text("${it.pricePerItem} $", color = textColor)
                        }
                    }

                    // Price Breakdown
                    Spacer(modifier = Modifier.height(12.dp))
                    SectionHeader("Price Breakdown")
                    Column(modifier = Modifier.padding(top = 6.dp)) {
                        Text("Raw: ${order.rawPrice} $", color = textColor)
                        Text("Tax: ${order.taxFee} $", color = textColor)
                        Text("Additional: ${order.additionalFee} $", color = textColor)
                        Text("Courier: ${order.courierFee} $", color = textColor)
                    }

                    // Address
                    Spacer(modifier = Modifier.height(12.dp))
                    SectionHeader("Delivery Address", icon = "📍")
                    Text(order.deliveryAddress, color = textColor, modifier = Modifier.padding(top = 4.dp))

                    // Review
                    if (order.restaurantStatus.equals("SERVED", ignoreCase = true)) {
                        order.review?.let { review ->
                            Spacer(modifier = Modifier.height(12.dp))
                            SectionHeader("Customer Review", icon = "⭐")

                            review.rating?.let {
                                Text("Rating: $it / 5", color = textColor)
                            }
                            review.comment?.let {
                                Text("Comment: $it", color = textColor)
                            }

                            val imageList = review.base64Images ?: emptyList()
                            if (imageList.isNotEmpty()) {
                                val pagerState = rememberPagerState(pageCount = { imageList.size })
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

                            Spacer(modifier = Modifier.height(8.dp))
                            if (!review.reply.isNullOrBlank()) {
                                Text("💬 Your Reply:", style = MaterialTheme.typography.titleSmall, color = textColor)
                                Text(review.reply!!, color = textColor)
                            } else if (!review.comment.isNullOrBlank()) {
                                OutlinedTextField(
                                    value = replyText,
                                    onValueChange = { replyText = it },
                                    label = { Text("Write your reply...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Button(
                                    onClick = {
                                        reply(order.review.id, replyText)
                                        replyText = ""
                                    },
                                    modifier = Modifier.align(Alignment.End).padding(top = 6.dp),
                                    enabled = replyText.isNotBlank()
                                ) {
                                    Text("Send Reply")
                                }
                            }
                        }
                    }
                }
            }

            // Buttons
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (order.restaurantStatus.uppercase()) {
                    "BASE" -> {
                        Button(onClick = onAccept) {
                            Text("Accept")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = onReject) {
                            Text("Reject")
                        }
                    }

                    "ACCEPTED" -> {
                        Button(
                            onClick = onServe,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Mark as Served", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: String = "•", color: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = color, modifier = Modifier.padding(end = 4.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = color)
    }
}
