package com.example.apfront.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.apfront.R
import com.example.apfront.data.remote.api.*
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.ui.screens.restaurant_dashboard.Base64Image
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val tabTitles = listOf("Users", "Restaurants", "Orders", "Transactions", "Coupons")
    var selectedTab by remember { mutableStateOf(0) }

    val usersResource by viewModel.users.collectAsState()
    val users = (usersResource as? Resource.Success)?.data ?: emptyList()

    val restaurantsResource by viewModel.restaurants.collectAsState()
    val restaurants = (restaurantsResource as? Resource.Success)?.data ?: emptyList()

    val ordersResource by viewModel.orders.collectAsState()
    val orders = (ordersResource as? Resource.Success)?.data ?: emptyList()

    val transactionsResource by viewModel.transactions.collectAsState()
    val transactions = (transactionsResource as? Resource.Success)?.data ?: emptyList()

    val couponsResource by viewModel.coupons.collectAsState()
    val coupons = (couponsResource as? Resource.Success)?.data ?: emptyList()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (selectedTab == 4) viewModel.fetchCoupons()
    }

    val userSearchQuery by viewModel.userSearchQuery.collectAsState()
    val restaurantSearchQuery by viewModel.restaurantSearchQuery.collectAsState()
    val orderSearchQuery by viewModel.orderSearchQuery.collectAsState()
    val transactionSearchQuery by viewModel.transactionSearchQuery.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 4) {
                FloatingActionButton(onClick = { navController.navigate("create_edit_coupon") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Coupon")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                0 -> viewModel.fetchUsers()
                                1 -> viewModel.fetchRestaurants()
                                2 -> viewModel.fetchOrders()
                                3 -> viewModel.fetchTransactions()
                                4 -> viewModel.fetchCoupons()
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val searchQuery = when (selectedTab) {
                0 -> userSearchQuery
                1 -> restaurantSearchQuery
                2 -> orderSearchQuery
                3 -> transactionSearchQuery
                else -> ""
            }

            if (selectedTab < 4) { // Show search for all but Coupons
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        when (selectedTab) {
                            0 -> viewModel.userSearchQuery.value = it
                            1 -> viewModel.restaurantSearchQuery.value = it
                            2 -> viewModel.orderSearchQuery.value = it
                            3 -> viewModel.transactionSearchQuery.value = it
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (selectedTab) {
                0 -> UserTab(users = users, onStatusChange = viewModel::updateUserStatus)
                1 -> RestaurantTab(restaurants = restaurants, onStatusChange = viewModel::updateRestaurantStatus)
                2 -> OrderTab(orders = orders)
                3 -> TransactionTab(transactions = transactions)
                4 -> CouponTab(coupons = coupons, navController = navController, viewModel = viewModel)
            }
        }
    }
}

// --- NEW RESTAURANT TAB ---
@Composable
fun RestaurantTab(restaurants: List<RestaurantDto>, onStatusChange: (Long, String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(restaurants) { restaurant ->
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    leadingContent = {
                        Base64Image(
                            base64Data = restaurant.logoBase64,
                            modifier = Modifier.size(56.dp).clip(CircleShape),
                            contentDescription = restaurant.name,
                            contentScale = ContentScale.Crop
                        )
                    },
                    headlineContent = { Text(restaurant.name) },
                    supportingContent = { Text("Phone: ${restaurant.phone}") },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            val status = restaurant.approvalStatus.uppercase()
                            Text(status, color = when (status) {
                                "REGISTERED" -> Color(0xFF006400)
                                "WAITING" -> Color(0xFFB8860B) // Dark golden
                                "REJECTED" -> MaterialTheme.colorScheme.error
                                "SUSPENDED" -> Color.Gray
                                else -> MaterialTheme.colorScheme.onBackground
                            })

                            Spacer(modifier = Modifier.height(4.dp))

                            Row {
                                when (status) {
                                    "WAITING", "SUSPENDED" -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp),    modifier = Modifier.width(110.dp), // You can adjust this width
                                        ) {
                                            Button(
                                                onClick = { onStatusChange(restaurant.id.toLong(), "registered") },
                                                modifier = Modifier.height(36.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text("Approve")
                                            }

                                            Button(
                                                onClick = { onStatusChange(restaurant.id.toLong(), "rejected") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                modifier = Modifier.height(36.dp)
                                                    .fillMaxWidth()                                            ) {
                                                Text("Reject")
                                            }
                                        }
                                    }

                                    "REGISTERED" -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp),    modifier = Modifier.width(110.dp), // You can adjust this width
                                        ) {
                                            Button(
                                                onClick = { onStatusChange(restaurant.id.toLong(), "suspended") },
                                                modifier = Modifier.height(36.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text("Suspend")
                                            }

                                            Button(
                                                onClick = { onStatusChange(restaurant.id.toLong(), "rejected") },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                modifier = Modifier.height(36.dp)
                                                    .fillMaxWidth()
                                            ) {
                                                Text("Reject")
                                            }
                                        }
                                    }

                                    "REJECTED" -> {
                                        Button(
                                            onClick = { onStatusChange(restaurant.id.toLong(), "registered") },
                                            modifier = Modifier.height(36.dp)
                                        ) { Text("Re-Approve") }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun UserTab(users: List<AdminUserDto>, onStatusChange: (String, String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users) { user ->
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text(user.fullName) },
                    supportingContent = { Text("Phone: ${user.phone}\nRole: ${user.role.uppercase()}") },
                    trailingContent = {
                        Column {
                            Text(user.status, color = if (user.status.equals("approved", true)) Color(0xFF006400) else MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Button(onClick = { onStatusChange(user.id, "approved") }, modifier = Modifier.height(36.dp)) {
                                    Text("Approve")
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Button(
                                    onClick = { onStatusChange(user.id, "rejected") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Reject")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun OrderTab(orders: List<AdminOrderDto>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(orders) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium)
                    Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodyMedium)
                    Text("Vendor: ${order.vendorName}", style = MaterialTheme.typography.bodyMedium)
                    Column (verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        AssistChip(onClick = {}, label = { Text("Overall Status: ${order.status}") })
                        AssistChip(onClick = {}, label = { Text("Restaurant Status: ${order.restaurantStatus}") })
                        AssistChip(onClick = {}, label = { Text("Delivery Status: ${order.deliveryStatus}") })
                    }
                    Text("Price: ${order.payPrice}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun TransactionTab(transactions: List<TransactionDto>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(transactions) { tx ->
            Card(modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Transaction #${tx.id}") },
                    supportingContent = { Text("Order ID: ${tx.orderId} - User ID: ${tx.userId}") },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(tx.status, color = if (tx.status.equals("success", true)) Color(0xFF006400) else MaterialTheme.colorScheme.error)
                            Text(tx.method)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CouponTab(
    coupons: List<CouponDto>,
    navController: NavController,
    viewModel: AdminViewModel
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(coupons) { coupon ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(coupon.couponCode, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Type: ${coupon.type} | Value: ${coupon.value}")
                    Text("Expires: ${coupon.endDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { navController.navigate("create_edit_coupon?couponId=${coupon.id}") }) {
                            Text("Edit")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.deleteCoupon(coupon.id.toString()) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
