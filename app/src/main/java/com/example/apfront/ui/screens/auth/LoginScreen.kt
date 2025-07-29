package com.example.apfront.ui.screens.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.R
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val activity = (LocalContext.current as? Activity)

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // Title is handled by the main text
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.Language, contentDescription = stringResource(R.string.language_switcher_description))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("English") }, onClick = {
                                viewModel.onLanguageSelected("en")
                                activity?.recreate()
                                expanded = false
                            })
                            DropdownMenuItem(text = { Text("فارسی") }, onClick = {
                                viewModel.onLanguageSelected("fa")
                                activity?.recreate()
                                expanded = false
                            })
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
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
                            logoTaps = 0
                        }
                    }
            )
            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(visible = !adminLoginVisible, enter = fadeIn(), exit = fadeOut()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.phone_number_label)) }, leadingIcon = { Icon(Icons.Default.Phone, null) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(R.string.password_label)) }, leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                }
            }

            AnimatedVisibility(visible = adminLoginVisible, enter = fadeIn(), exit = fadeOut()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = adminPhone, onValueChange = { adminPhone = it },
                        label = { Text(stringResource(R.string.admin_username_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = adminPassword, onValueChange = { adminPassword = it },
                        label = { Text(stringResource(R.string.admin_password_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (loginState is Resource.Error) {
                Text(
                    text = loginState.message ?: stringResource(R.string.login_error),
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
                shape = RoundedCornerShape(12.dp),
                enabled = loginState !is Resource.Loading
            ) {
                if (loginState is Resource.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.login_button), style = MaterialTheme.typography.titleMedium)
                }
            }

            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.register_prompt))
            }
        }
    }
}