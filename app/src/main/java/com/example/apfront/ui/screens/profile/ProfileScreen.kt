package com.example.apfront.ui.screens.profile

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.apfront.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.profile_title)) }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading && uiState.user == null) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else if (uiState.user != null) {
                if (uiState.user?.role?.equals("ADMIN", ignoreCase = true) == true) {
                    AdminProfileContent(user = uiState.user!!, onLogout = {
                        viewModel.logout()
                        onLogout()
                    })
                } else {
                    UserProfileContent(navController = navController, onLogout = onLogout, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AdminProfileContent(user: com.example.apfront.data.remote.dto.UserDto, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = "Admin",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(user.fullName ?: "Admin", style = MaterialTheme.typography.headlineSmall)
        Text(user.phone ?: "admin", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun UserProfileContent(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- THIS IS THE FIX for the language switcher ---
    // Get a reference to the current Activity so we can restart it.
    val activity = (LocalContext.current as? Activity)
    // --- END OF FIX ---

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageSelected(uri) }
    )

    LaunchedEffect(uiState.user) {
        uiState.user?.let {
            fullName = it.fullName ?: ""
            phone = it.phone ?: ""
            email = it.email ?: ""
            address = it.address ?: ""
            bankName = it.bankInfo?.bankName ?: ""
            accountNumber = it.bankInfo?.accountNumber ?: ""
        }
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = uiState.selectedImageUri ?: uiState.user?.profileImageBase64,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_avatar_placeholder),
            error = painterResource(id = R.drawable.ic_avatar_placeholder)
        )
        TextButton(onClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text(stringResource(R.string.change_picture_button))
        }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.user?.role?.equals("SELLER", ignoreCase = true) == true || uiState.user?.role?.equals("COURIER", ignoreCase = true) == true) {
            Text("Bank Information", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account Number") }, modifier = Modifier.fillMaxWidth())
        }

        Divider(modifier = Modifier.padding(vertical = 24.dp))
        ListItem(
            headlineContent = { Text("My Favorites") },
            modifier = Modifier.clickable { navController.navigate("favorites") }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Language", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            // --- THIS IS THE FIX ---
            Button(onClick = {
                viewModel.onLanguageSelected("en")
                activity?.recreate() // Restart the activity to apply the language change
            }) { Text("English") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                viewModel.onLanguageSelected("fa")
                activity?.recreate() // Restart the activity to apply the language change
            }) { Text("فارسی") }
            // --- END OF FIX ---
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.updateProfile(fullName, phone, email, address, bankName, accountNumber) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Save Changes")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
