package com.example.apfront.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.R
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    // --- FIX: Add the onNavigateToRegister parameter here ---
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    // --- END OF FIX ---
    viewModel: LoginViewModel = hiltViewModel()
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    // This LaunchedEffect block handles navigation after a successful login
    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            val role = loginState.data?.user?.role ?: "BUYER" // Default to BUYER if role is missing
            onLoginSuccess(role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.login), style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.phone_label)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (val state = loginState) {
            is Resource.Loading -> {
                CircularProgressIndicator()
            }
            is Resource.Error -> {
                Text(state.message ?: "An error occurred", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                // No need to show anything for Success or Idle here
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(phone, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is Resource.Loading
        ) {
            Text(stringResource(R.string.login_button))
        }

        // This button will now work correctly
        TextButton(onClick = onNavigateToRegister) {
            Text(stringResource(R.string.sign_up_button))
        }
    }
}