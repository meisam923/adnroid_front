package com.example.apfront.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.apfront.MainViewModel
import com.example.apfront.R
import com.example.apfront.ui.navigation.BottomNavItem
import com.example.apfront.ui.screens.admin_hub.AdminScreen
import com.example.apfront.ui.screens.admin_hub.CreateEditCouponScreen
import com.example.apfront.ui.screens.checkout.CheckoutScreen
import com.example.apfront.ui.screens.courier_hub.CourierHubScreen
import com.example.apfront.ui.screens.editrating.EditRatingScreen
import com.example.apfront.ui.screens.favorites.FavoritesScreen
import com.example.apfront.ui.screens.itemdetail.ItemDetailScreen
import com.example.apfront.ui.screens.itemlist.ItemListScreen
import com.example.apfront.ui.screens.notifications.NotificationsScreen
import com.example.apfront.ui.screens.onlinepayment.OnlinePaymentScreen
import com.example.apfront.ui.screens.orderdetail.OrderDetailScreen
import com.example.apfront.ui.screens.orderdetail.OrderSuccessScreen
import com.example.apfront.ui.screens.orderhistory.OrderHistoryScreen
import com.example.apfront.ui.screens.profile.ProfileScreen
import com.example.apfront.ui.screens.restaurant_dashboard.CreateEditItemScreen
import com.example.apfront.ui.screens.restaurantdetail.RestaurantDetailScreen
import com.example.apfront.ui.screens.seller_hub.SellerHubScreen
import com.example.apfront.ui.screens.submitrating.SubmitRatingScreen
import com.example.apfront.ui.screens.vendorlist.VendorListScreen
import com.example.apfront.ui.screens.wallet.WalletScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userRole: String,
    rootNavController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val uiState by mainViewModel.uiState.collectAsState()


    val bottomNavItems = when (userRole.uppercase()) {
        "BUYER" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Orders,
            BottomNavItem.Wallet,
            BottomNavItem.Profile
        )
        "SELLER" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile
        )
        "COURIER" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile
        )
        "ADMIN" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Profile
        )
        else -> emptyList()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        BadgedBox(
                            badge = {
                                if (uiState.unreadNotificationCount > 0) {
                                    Badge { Text("${uiState.unreadNotificationCount}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.notifications_title))
                        }
                    }
                }
            )
        },bottomBar = {
            if (bottomNavItems.isNotEmpty()) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(id = screen.labelResId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                when (userRole.uppercase()) {
                    "SELLER" -> SellerHubScreen(navController = navController)
                    "BUYER" -> VendorListScreen(navController = navController)
                    "COURIER" -> CourierHubScreen(navController = navController)
                    "ADMIN" -> AdminScreen(navController = navController)
                }
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(navController = navController, onLogout = {
                    rootNavController.navigate("auth_flow") {
                        popUpTo("main_flow/{userRole}") { inclusive = true }
                    }
                })
            }
            composable(BottomNavItem.Search.route) {
                ItemListScreen(navController = navController)
            }
            composable(BottomNavItem.Orders.route) {
                OrderHistoryScreen(navController = navController)
            }
            composable(BottomNavItem.Wallet.route) {
                WalletScreen(navController = navController)
            }
            composable(
                route = "restaurant_detail/{restaurantId}",
                arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("restaurantId") ?: -1
                RestaurantDetailScreen(navController = navController)
            }
            composable(
                route = "order_detail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) {
                OrderDetailScreen(navController = navController)
            }
            composable(route = "checkout") {
                CheckoutScreen(navController = navController)
            }
            composable(
                route = "order_success/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) {
                OrderSuccessScreen(navController = navController)
            }
            composable(
                route = "item_detail/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) {
                ItemDetailScreen(navController = navController)
            }
            composable(
                route = "online_payment/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) {
                OnlinePaymentScreen(
                    navController = navController,
                    orderId = it.arguments?.getLong("orderId") ?: -1
                )
            }
            composable("favorites") {
                FavoritesScreen(navController = navController)
            }
            composable(
                route = "create_edit_item/{restaurantId}?itemId={itemId}",
                arguments = listOf(
                    navArgument("restaurantId") { type = NavType.IntType },
                    navArgument("itemId") { type = NavType.IntType; defaultValue = -1 }
                )
            ) { backStackEntry ->
                val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: 0
                CreateEditItemScreen(
                    navController = navController,
                    restaurantId = restaurantId
                )
            }
            composable(
                route = "create_edit_coupon?couponId={couponId}",
                arguments = listOf(
                    navArgument("couponId") { type = NavType.StringType; nullable = true }
                )
            ) {
                CreateEditCouponScreen(navController = navController)
            }
            composable("notifications") {
                NotificationsScreen(navController = navController, mainViewModel = mainViewModel)
            }
            composable(
                route = "edit_rating/{ratingId}",
                arguments = listOf(navArgument("ratingId") { type = NavType.LongType })
            ) {
                EditRatingScreen(navController = navController)
            }
            composable(
                route = "submit_rating/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) {
                SubmitRatingScreen(navController = navController)
            }

        }
    }
}
