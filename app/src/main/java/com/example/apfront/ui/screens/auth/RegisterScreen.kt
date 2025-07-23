package com.example.apfront.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (role: String) -> Unit
) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val registerState by viewModel.registerState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    val roles = listOf("BUYER", "SELLER", "COURIER")
    var selectedRole by remember { mutableStateOf(roles[0]) }

    LaunchedEffect(registerState) {
        if (registerState is com.example.apfront.util.Resource.Success) {
            onRegisterSuccess(selectedRole)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Your Account") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Personal Information Section
            Text("Personal Information", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email (Optional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // Role Selection Section
            Text("Account Type", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Text("Please select the type of account you want to create.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())

            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                roles.forEach { role ->
                    Row(
                        Modifier
                            .selectable(selected = (role == selectedRole), onClick = { selectedRole = role }, role = Role.RadioButton)
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (role == selectedRole), onClick = null)
                        Text(text = role, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            // Conditional Bank Information Section
            AnimatedVisibility(visible = selectedRole == "SELLER" || selectedRole == "COURIER") {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                    Text("Bank Information (for Payouts)", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account Number") }, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.registerUser(fullName, phone, email.ifBlank { null }, password, selectedRole, address)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = registerState !is com.example.apfront.util.Resource.Loading
            ) {
                Text("Register", style = MaterialTheme.typography.titleMedium)
            }

            if (registerState is com.example.apfront.util.Resource.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            if (registerState is com.example.apfront.util.Resource.Error) {
                Text(
                    text = registerState.message ?: "An unknown error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}