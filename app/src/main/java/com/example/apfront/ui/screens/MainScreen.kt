package com.example.apfront.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.apfront.ui.navigation.BottomNavItem
import com.example.apfront.ui.screens.checkout.CheckoutScreen
import com.example.apfront.ui.screens.courier_hub.CourierHubScreen
import com.example.apfront.ui.screens.favorites.FavoritesScreen
import com.example.apfront.ui.screens.itemdetail.ItemDetailScreen
import com.example.apfront.ui.screens.itemlist.ItemListScreen
import com.example.apfront.ui.screens.onlinepayment.OnlinePaymentScreen
import com.example.apfront.ui.screens.orderdetail.OrderDetailScreen
import com.example.apfront.ui.screens.orderdetail.OrderSuccessScreen
import com.example.apfront.ui.screens.orderhistory.OrderHistoryScreen
import com.example.apfront.ui.screens.profile.ProfileScreen
import com.example.apfront.ui.screens.restaurantdetail.RestaurantDetailScreen
import com.example.apfront.ui.screens.seller_hub.SellerHubScreen
import com.example.apfront.ui.screens.vendorlist.VendorListScreen
import com.example.apfront.ui.screens.wallet.WalletScreen

@Composable
fun MainScreen(
    userRole: String,
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // --- THIS IS THE FIX ---
    // We now define different sets of navigation items based on the user's role.
    val bottomNavItems = when (userRole.uppercase()) {
        "BUYER" -> listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.Orders,
            BottomNavItem.Wallet,
            BottomNavItem.Profile
        )
        "SELLER" -> listOf(
            BottomNavItem.Home, // Will point to SellerHub
            BottomNavItem.Profile
        )
        "COURIER" -> listOf(
            BottomNavItem.Home, // Will point to CourierHub
            BottomNavItem.Profile
        )
        else -> emptyList() // Default case
    }
    // --- END OF FIX ---

    Scaffold(
        bottomBar = {
            // Only show the bottom bar if there are items to display for the role
            if (bottomNavItems.isNotEmpty()) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
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
                // --- THIS IS THE FIX ---
                // The "Home" route now correctly shows the right screen for each role.
                when (userRole.uppercase()) {
                    "SELLER" -> SellerHubScreen(navController = navController)
                    "BUYER" -> VendorListScreen(navController = navController)
                    "COURIER" -> CourierHubScreen(navController = navController)
                }
                // --- END OF FIX ---
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
        }
    }
}
