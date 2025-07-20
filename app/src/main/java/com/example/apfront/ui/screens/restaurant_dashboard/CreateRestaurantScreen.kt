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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.res.stringResource
import com.example.apfront.R
import com.example.apfront.util.uriToBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRestaurantScreen(
    onSuccess : ()-> Unit,
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

    LaunchedEffect(createState) {
        if (createState is Resource.Success) {
            onSuccess()
        }
    }

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
        Text(stringResource(R.string.create_restaurant_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // --- Text Inputs ---
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.restaurant_name_label)) }, modifier = Modifier.fillMaxWidth() ,shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.address_label)) }, modifier = Modifier.fillMaxWidth(),shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(stringResource(R.string.phone_label)) }, modifier = Modifier.fillMaxWidth(),shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = taxFee, onValueChange = { taxFee = it }, label = { Text(stringResource(R.string.tax_fee_label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(),shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = additionalFee, onValueChange = { additionalFee = it }, label = { Text(stringResource(R.string.additional_fee_label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(),shape = RoundedCornerShape(16.dp))
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
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
        }) {
            Icon(
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = "Select Image Icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.select_image_button))
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
                    val logoBase64 :String? = selectedImageUri?.let { uriToBase64(context, it) }
                    viewModel.createRestaurant(token, name, address, phone, tax, additional, logoBase64)
                }
            },
            enabled = createState !is Resource.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_restaurant_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = createState) {
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> {
                LaunchedEffect(state) {
                    val message = context.getString(R.string.restaurant_created_toast, state.data?.name)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
            is Resource.Error -> {
                val errorMessage = when (state.message) {
                    "error_400_invalid_input" -> stringResource(R.string.error_400_invalid_input)
                    "error_401_unauthorized" -> stringResource(R.string.error_401_unauthorized)
                    "error_403_forbidden" -> stringResource(R.string.error_403_forbidden)
                    "error_404_not_found" -> stringResource(R.string.error_404_not_found)
                    "error_409_conflict" -> stringResource(R.string.error_409_conflict)
                    "error_500_server_error" -> stringResource(R.string.error_500_server_error)
                    "error_network_connection" -> stringResource(R.string.error_network_connection)
                    else -> stringResource(R.string.error_unknown)
                }

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is Resource.Idle -> {}
        }
    }
}

// Helper function to convert a Uri to a Base64 string
