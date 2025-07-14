package com.example.apfront.ui.screens.restaurant_dashboard

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.apfront.util.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRestaurantScreen(
    viewModel: CreateRestaurantViewModel = hiltViewModel()
) {
    // State for text fields
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var taxFee by remember { mutableStateOf("") }
    var additionalFee by remember { mutableStateOf("") }

    // State for image picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val createState by viewModel.createState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // IMPORTANT: You need to pass the real auth token here
    val token = "your_saved_auth_token"

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make the column scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create a New Restaurant", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Text Inputs ---
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Restaurant Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = taxFee, onValueChange = { taxFee = it }, label = { Text("Tax Fee") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = additionalFee, onValueChange = { additionalFee = it }, label = { Text("Additional Fee") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // --- Image Picker ---
        if (selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Select Image")
        }
        Spacer(modifier = Modifier.height(24.dp))


        // --- Create Button and State Handling ---
        Button(
            onClick = {
                // Safely convert text to Int
                val tax = taxFee.toIntOrNull() ?: 0
                val additional = additionalFee.toIntOrNull() ?: 0

                // Launch a coroutine to handle Base64 conversion
                coroutineScope.launch {
                    val logoBase64 = selectedImageUri?.let { uriToBase64(context, it) }
                    viewModel.createRestaurant(token, name, address, phone, tax, additional, logoBase64)
                }
            },
            enabled = createState !is Resource.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Restaurant")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = createState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> {
                LaunchedEffect(state) {
                    Toast.makeText(context, "Restaurant '${state.data?.name}' created!", Toast.LENGTH_LONG).show()
                }
            }
            is Resource.Error -> {
                Text(text = state.message ?: "An error occurred", color = MaterialTheme.colorScheme.error)
            }
            is Resource.Idle -> {}
        }
    }
}

// Helper function to convert a Uri to a Base64 string
private fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}