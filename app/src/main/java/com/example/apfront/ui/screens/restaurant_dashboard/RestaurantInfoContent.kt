package com.example.apfront.ui.screens.restaurant_dashboard

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.apfront.R
import com.example.apfront.data.remote.dto.RestaurantDto
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfoContent(
    restaurant: RestaurantDto,
    viewModel: RestaurantInfoViewModel = hiltViewModel()
) {
    // Collect state from the ViewModel
    val name by viewModel.name.collectAsState()
    val address by viewModel.address.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val taxFee by viewModel.taxFee.collectAsState()
    val additionalFee by viewModel.additionalFee.collectAsState()
    val imageUri by viewModel.logoImageUri.collectAsState()
    val existingLogo by viewModel.existingLogoBase64.collectAsState()

    val updateState by viewModel.updateState.collectAsState()
    val context = LocalContext.current

    // Pre-fill the form once when the screen appears or the restaurant data changes
    LaunchedEffect(restaurant) {
        viewModel.loadRestaurantInfo(restaurant)
    }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.logoImageUri.value = uri }
    )

    // Determine what image to show: the newly selected one, the existing one, or a placeholder
    val painter = rememberAsyncImagePainter(
        model = imageUri ?: existingLogo?.let { "data:image/jpeg;base64,$it" } ?: R.drawable.ic_launcher_background
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
    ) {
        Text("Edit Your Restaurant's Information", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Image Preview and Button
        Image(
            painter = painter,
            contentDescription = "Restaurant Logo",
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(id = R.string.change_image_button))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Text Fields
        OutlinedTextField(value = name, onValueChange = { viewModel.name.value = it }, label = { Text(stringResource(R.string.restaurant_name_label)) }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = address, onValueChange = { viewModel.address.value = it }, label = { Text(stringResource(R.string.address_label)) }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { viewModel.phone.value = it }, label = { Text(stringResource(R.string.phone_label)) }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = taxFee, onValueChange = { viewModel.taxFee.value = it }, label = { Text(stringResource(R.string.tax_fee_label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = additionalFee, onValueChange = { viewModel.additionalFee.value = it }, label = { Text(stringResource(R.string.additional_fee_label)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        // Save Button and Loading Indicator
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.onUpdateClicked(restaurant.id, context) },
                enabled = updateState !is Resource.Loading,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("Save Changes")
            }
            if (updateState is Resource.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    // Show a toast message on success
    LaunchedEffect(updateState) {
        if (updateState is Resource.Success) {
            Toast.makeText(context, "Details updated successfully!", Toast.LENGTH_SHORT).show()
        }
        if (updateState is Resource.Error)
            Toast.makeText(context, "There is an error ,check the fields", Toast.LENGTH_SHORT).show()
        }
    }