package com.example.apfront

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.apfront.ui.navigation.BottomNavItem
import com.example.apfront.ui.screens.admin.AdminScreen
import com.example.apfront.ui.screens.admin.CreateEditCouponScreen
import com.example.apfront.ui.screens.auth.LoginScreen
import com.example.apfront.ui.screens.auth.RegisterScreen
import com.example.apfront.ui.screens.profile.ProfileScreen
import com.example.apfront.ui.screens.restaurant_dashboard.CreateEditItemScreen
import com.example.apfront.ui.screens.restaurantdetail.RestaurantDetailScreen
import com.example.apfront.ui.screens.seller_hub.SellerHubScreen
import com.example.apfront.ui.screens.vendorlist.VendorListScreen
import com.example.apfront.ui.theme.ApFrontTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApFrontTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This is the single entry point for the app's entire UI
                    AppNavigation()
                }
            }
        }
    }
}

/**
 * This is the top-level router for the application. It decides whether to show
 * the authentication flow (login/register) or the main, logged-in flow.
 */
@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val startState by viewModel.startState.collectAsState()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            // This screen decides the initial route based on login status.
            LaunchedEffect(startState) {
                when (val state = startState) {
                    is AppStartState.UserLoggedIn -> {
                        // If user is already logged in, go to the main flow, passing the role
                        navController.navigate("main_flow/${state.role}") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    is AppStartState.UserLoggedOut -> {
                        // If logged out, go to the authentication flow
                        navController.navigate("auth_flow") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    is AppStartState.Loading -> { /* Wait for state change */ }
                }
            }
            // Show a loading indicator while deciding
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        // The "auth_flow" is a nested graph for login and register screens
        navigation(startDestination = "login", route = "auth_flow") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { role ->
                        navController.navigate("main_flow/$role") {
                            popUpTo("auth_flow") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { role ->
                        navController.navigate("main_flow/$role") {
                            popUpTo("auth_flow") { inclusive = true }
                        }
                    }
                )
            }
        }

        // The "main_flow" is the destination for the entire logged-in experience
        composable("main_flow/{userRole}") { backStackEntry ->
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "BUYER"
            MainScreen(
                userRole = userRole,
                onLogout = {
                    navController.navigate("auth_flow") {
                        popUpTo("main_flow/{userRole}") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * This composable represents the main screen for a logged-in user.
 * It contains the bottom navigation bar and the screens that can be accessed from it.
 */
@Composable
fun MainScreen(
    userRole: String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile,
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
        // This is the INNER NavHost for the logged-in part of the app
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                when (userRole.uppercase()) {
                    "SELLER" -> SellerHubScreen(navController = navController)
                    "BUYER" -> VendorListScreen(navController = navController)
                    "ADMIN" -> AdminScreen(navController = navController,
                        viewModel = hiltViewModel())
                    else -> Text("Unknown user role")
                }
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    onLogout = onLogout
                )
            }
            composable(
                route = "restaurant_detail/{restaurantId}",
                arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
            ) {
                RestaurantDetailScreen(navController = navController)
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
                // The route can have an optional couponId for editing
                route = "create_edit_coupon?couponId={couponId}",
                arguments = listOf(
                    navArgument("couponId") { type = NavType.StringType; nullable = true }
                )
            ) {
                CreateEditCouponScreen(navController = navController)
            }
        }
    }
}
