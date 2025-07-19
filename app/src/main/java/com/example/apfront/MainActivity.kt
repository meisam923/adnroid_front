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
import androidx.navigation.compose.*
import com.example.apfront.ui.navigation.BottomNavItem
import com.example.apfront.ui.screens.auth.LoginScreen
import com.example.apfront.ui.screens.auth.RegisterScreen
import com.example.apfront.ui.screens.profile.ProfileScreen
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
                    // The root of our app's UI
                    AppEntry()
                }
            }
        }
    }
}

@Composable
fun AppEntry(
    viewModel: MainViewModel = hiltViewModel()
) {
    val startState by viewModel.startState.collectAsState()
    val navController = rememberNavController()

    // This is the main router for the entire application.
    // It decides whether to show the "logged out" flow or the "logged in" flow.
    NavHost(navController = navController, startDestination = "auth_flow") {

        // The "auth_flow" contains the Login and Register screens
        navigation(startDestination = "login", route = "auth_flow") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { role ->
                        // After login, navigate to the main flow and clear the auth flow from history
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

        // The "main_flow" is a single destination that holds our entire logged-in experience
        composable("main_flow/{userRole}") { backStackEntry ->
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "BUYER"
            MainScreen(userRole = userRole, onLogout = {
                // When the user logs out from the MainScreen, navigate back to the auth flow
                navController.navigate("auth_flow") {
                    popUpTo("main_flow/{userRole}") { inclusive = true }
                }
            })
        }
    }

    // This effect runs when the app starts to check the initial login state
    LaunchedEffect(startState) {
        when (val state = startState) {
            is AppStartState.UserLoggedIn -> {
                // If already logged in, go directly to the main flow
                navController.navigate("main_flow/${state.role}") {
                    popUpTo("auth_flow") { inclusive = true }
                }
            }
            // If loading or logged out, the NavHost will correctly start at the "auth_flow"
            else -> {}
        }
    }
}

@Composable
fun MainScreen(
    userRole: String,
    onLogout: () -> Unit // Callback to signal a logout event
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
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
        // This is the inner NavHost for the screens accessible via the bottom bar.
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                when (userRole.uppercase()) {
                    "SELLER" -> SellerHubScreen(navController = navController)
                    "BUYER" -> VendorListScreen(navController = navController)
                }
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    onLogout = onLogout // Pass the logout callback down
                )
            }
        }
    }
}