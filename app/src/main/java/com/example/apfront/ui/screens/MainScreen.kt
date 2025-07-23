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
import com.example.apfront.ui.screens.editrating.EditRatingScreen
import com.example.apfront.ui.screens.favorites.FavoritesScreen
import com.example.apfront.ui.screens.itemlist.ItemListScreen
import com.example.apfront.ui.screens.orderdetail.OrderDetailScreen
import com.example.apfront.ui.screens.orderdetail.OrderSuccessScreen
import com.example.apfront.ui.screens.orderhistory.OrderHistoryScreen
import com.example.apfront.ui.screens.profile.ProfileScreen
import com.example.apfront.ui.screens.restaurantdetail.RestaurantDetailScreen
import com.example.apfront.ui.screens.seller_hub.SellerHubScreen
import com.example.apfront.ui.screens.submitrating.SubmitRatingScreen
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

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Orders,
        BottomNavItem.Wallet,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
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
                }
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    onLogout = {
                        rootNavController.navigate("auth_flow") {
                            popUpTo("main_flow/{userRole}") { inclusive = true }
                        }
                    }
                )
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
            ) {
                // --- THIS IS THE FIX ---
                // The RestaurantDetailScreen does not need the ID passed to it directly.
                // Hilt and its ViewModel will get it from the backStackEntry automatically.
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
            composable("favorites") {
                FavoritesScreen(navController = navController)
            }
            composable(
                route = "submit_rating/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) {
                SubmitRatingScreen(navController = navController)
            }
            composable(
                route = "edit_rating/{ratingId}",
                arguments = listOf(navArgument("ratingId") { type = NavType.LongType })
            ) {
                EditRatingScreen(navController = navController)
            }
        }
    }
}