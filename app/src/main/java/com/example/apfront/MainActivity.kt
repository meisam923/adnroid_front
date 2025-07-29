
package com.example.apfront

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import com.example.apfront.ui.screens.MainScreen
import com.example.apfront.ui.screens.auth.LoginScreen
import com.example.apfront.ui.screens.auth.RegisterScreen
import com.example.apfront.ui.screens.forgotpassword.ForgotPasswordScreen
import com.example.apfront.ui.screens.resetpassword.ResetPasswordScreen
import com.example.apfront.ui.theme.ApFrontTheme
import com.example.apfront.util.LocaleManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class MainActivity : AppCompatActivity() {

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
                    navController = navController, // Pass the NavController
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
        composable("forgot_password") {
            ForgotPasswordScreen(
                navController = navController,
                onCodeSent = { email ->
                    navController.navigate("reset_password/$email")
                }
            )
        }
        composable(
            route = "reset_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController = navController, email = email)
        }
    }
}