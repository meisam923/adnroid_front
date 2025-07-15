package com.example.apfront

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apfront.ui.screens.auth.LoginScreen
import com.example.apfront.ui.screens.seller_hub.SellerHubScreen
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
                    // This is the root composable that handles navigation logic
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

    // This block determines what to show when the app starts
    when (startState) {
        is AppStartState.Loading -> {
            // Show a loading spinner while checking for a saved token
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is AppStartState.UserLoggedIn, is AppStartState.UserLoggedOut -> {
            // Determine the starting screen based on the login state
            val startDestination = if (startState is AppStartState.UserLoggedIn) {
                "seller_hub" // If logged in, go directly to the seller hub
            } else {
                "login" // If logged out, go to login
            }

            // This NavHost contains all the possible screens
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(route = "login") {
                    LoginScreen(navController = navController)
                }
                composable(route = "seller_hub") {
                    SellerHubScreen(navController = navController)
                }
            }
        }
    }
}