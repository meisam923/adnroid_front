package com.example.apfront.ui.screens.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (role: String) -> Unit
) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val registerState by viewModel.registerState.collectAsState()
    val context = LocalContext.current
    val activity = (context as? Activity)

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    val roles = listOf("BUYER", "SELLER", "COURIER")
    var selectedRole by remember { mutableStateOf(roles[0]) }
    var isPhoneError by remember { mutableStateOf(false) }


    LaunchedEffect(registerState) {
        if (registerState is com.example.apfront.util.Resource.Success) {
            onRegisterSuccess(selectedRole)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_account_title)) },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.Language, contentDescription = stringResource(R.string.language_switcher_description))
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("English") },
                                onClick = {
                                    viewModel.onLanguageSelected("en")
                                    activity?.recreate()
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("فارسی") },
                                onClick = {
                                    viewModel.onLanguageSelected("fa")
                                    activity?.recreate()
                                    expanded = false
                                }
                            )
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.personal_information_label), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text(stringResource(R.string.full_name_label)) }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 11) {
                                phone = it
                                isPhoneError = it.length != 11
                            }
                        },
                        label = { Text(stringResource(R.string.phone_number_label)) },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = isPhoneError,
                        supportingText = {
                            if (isPhoneError) {
                                Text("Phone number must be 11 digits.")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.email_optional_label)) }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(stringResource(R.string.password_label)) }, leadingIcon = { Icon(Icons.Default.Lock, null) }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.address_label)) }, leadingIcon = { Icon(Icons.Default.Home, null) }, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.account_type_label), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.account_type_prompt), style = MaterialTheme.typography.bodyMedium)

                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                        roles.forEach { role ->
                            Row(
                                Modifier.selectable(selected = (role == selectedRole), onClick = { selectedRole = role }, role = Role.RadioButton),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = (role == selectedRole), onClick = null)
                                Text(text = stringResource(id = getRoleStringRes(role)), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedRole == "SELLER" || selectedRole == "COURIER",
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500), initialOffsetY = { -40 })
            ) {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.bank_info_label), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text(stringResource(R.string.bank_name_label)) }, leadingIcon = { Icon(Icons.Default.AccountBalance, null) }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text(stringResource(R.string.account_number_label)) }, leadingIcon = { Icon(Icons.Default.CreditCard, null) }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.registerUser(fullName, phone, email.ifBlank { null }, password, selectedRole, address, bankName, accountNumber)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = registerState !is com.example.apfront.util.Resource.Loading
            ) {
                Text(stringResource(R.string.register_button), style = MaterialTheme.typography.titleMedium)
            }

            if (registerState is com.example.apfront.util.Resource.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            if (registerState is com.example.apfront.util.Resource.Error) {
                Text(
                    text = registerState.message ?: stringResource(R.string.unknown_error),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun getRoleStringRes(role: String): Int {
    return when (role) {
        "SELLER" -> R.string.role_seller
        "COURIER" -> R.string.role_courier
        else -> R.string.role_buyer
    }
}
