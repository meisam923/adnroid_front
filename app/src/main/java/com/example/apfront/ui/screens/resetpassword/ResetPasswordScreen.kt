package com.example.apfront.ui.screens.resetpassword

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController, email: String) {
    val viewModel: ResetPasswordViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is Resource.Success) {
            Toast.makeText(context, context.getString(R.string.password_reset_success), Toast.LENGTH_LONG).show()
            navController.navigate("login") { popUpTo("auth_flow") { inclusive = true } }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.forgot_password_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back_description))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(stringResource(R.string.reset_password_instructions))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text(stringResource(R.string.reset_code_label)) }, leadingIcon = { Icon(Icons.Default.Pin, null) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text(stringResource(R.string.new_password_label)) }, leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.resetPassword(email, code, newPassword) },
                enabled = state !is Resource.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state is Resource.Loading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text(stringResource(R.string.confirm_password_button))
            }
            if (state is Resource.Error) {
                Text((state as Resource.Error).message ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}