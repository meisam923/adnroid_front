// STEP 1: Replace the entire content of your MainActivity.kt with this final version.
// This version is clean and correctly calls the one true MainScreen.

package com.example.apfront

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.apfront.ui.screens.MainScreen // Import the correct MainScreen from its own file
import com.example.apfront.ui.screens.auth.LoginScreen
import com.example.apfront.ui.screens.auth.RegisterScreen
import com.example.apfront.ui.theme.ApFrontTheme
import com.example.apfront.util.LocaleManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var localeManager: LocaleManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeManager.applyLocaleOnStartup()
        setContent {
            ApFrontTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

/**
 * This is the top-level router for the application. It decides whether to show
 * the authentication flow or the main, logged-in flow.
 */
@Composable
fun AppNavigation(
    viewModel: MainViewModel = hiltViewModel()
) {
    val startState by viewModel.startState.collectAsState()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            LaunchedEffect(startState) {
                when (val state = startState) {
                    is AppStartState.UserLoggedIn -> {
                        navController.navigate("main_flow/${state.role}") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    is AppStartState.UserLoggedOut -> {
                        navController.navigate("auth_flow") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    is AppStartState.Loading -> {}
                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

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

        // The "main_flow" route now correctly calls the external MainScreen
        composable(
            route = "main_flow/{userRole}",
            arguments = listOf(navArgument("userRole") { type = NavType.StringType })
        ) { backStackEntry ->
            val userRole = backStackEntry.arguments?.getString("userRole") ?: "BUYER"
            MainScreen(
                userRole = userRole,
                rootNavController = navController
            )
        }
    }
}