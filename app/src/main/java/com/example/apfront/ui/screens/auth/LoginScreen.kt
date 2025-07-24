package com.example.apfront.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("") }

    var logoTaps by remember { mutableStateOf(0) }
    var adminLoginVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            val role = loginState.data?.user?.role ?: "BUYER"
            onLoginSuccess(role)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Poly Eats",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .scale(scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        logoTaps++
                        if (logoTaps >= 7) {
                            adminLoginVisible = !adminLoginVisible
                        }
                    }
            )
            Text(
                text = "Taste the convenience.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(visible = !adminLoginVisible, enter = fadeIn(), exit = fadeOut()) {
                Column {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                }
            }

            AnimatedVisibility(visible = adminLoginVisible, enter = fadeIn(), exit = fadeOut()) {
                Column {
                    OutlinedTextField(
                        value = adminPhone,
                        onValueChange = { adminPhone = it },
                        label = { Text("Admin Username") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Admin Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (loginState is Resource.Error) {
                Text(
                    text = loginState.message ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (adminLoginVisible) {
                        viewModel.login(adminPhone, adminPassword)
                    } else {
                        viewModel.login(phone, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = loginState !is Resource.Loading
            ) {
                if (loginState is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Login", style = MaterialTheme.typography.titleMedium)
                }
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }
        }
    }
}