package com.example.apfront.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Orders : BottomNavItem("orders", Icons.Default.Receipt, "Orders")
    object Wallet : BottomNavItem("wallet", Icons.Default.AccountBalanceWallet, "Wallet")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}