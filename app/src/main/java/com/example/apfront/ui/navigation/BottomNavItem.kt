package com.example.apfront.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.apfront.R

sealed class BottomNavItem(val route: String, val icon: ImageVector, @StringRes val labelResId: Int) {
    object Home : BottomNavItem("home", Icons.Default.Home, R.string.bottom_nav_home)
    object Search : BottomNavItem("search", Icons.Default.Search, R.string.bottom_nav_search)
    object Orders : BottomNavItem("orders", Icons.Default.Receipt, R.string.bottom_nav_orders)
    object Wallet : BottomNavItem("wallet", Icons.Default.AccountBalanceWallet, R.string.bottom_nav_wallet)
    object Profile : BottomNavItem("profile", Icons.Default.Person, R.string.bottom_nav_profile)
}