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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.apfront.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditItemScreen(
    navController: NavController,
    restaurantId: Int,
    viewModel: CreateEditItemViewModel = hiltViewModel()
) {
    // Collect all the state from the ViewModel
    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val price by viewModel.price.collectAsState()
    val supply by viewModel.supply.collectAsState()
    val keywords by viewModel.keywords.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val existingImageUrl by viewModel.existingImageUrl.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    // This effect runs once to load the item's data if we are in "Edit" mode
    LaunchedEffect(key1 = Unit) {
        viewModel.loadItemForEditing(restaurantId)
    }

    // This effect handles navigating back after a successful save
    LaunchedEffect(saveState) {
        if (saveState is SaveItemState.Success) {
            Toast.makeText(context, "Item saved successfully!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.imageUri.value = uri }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add/Edit Item") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- Image Preview and Picker Button ---
            val hasImage = imageUri != null || existingImageUrl != null

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (hasImage) {
                    when {
                        imageUri != null -> {
                            // Show image from gallery
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        existingImageUrl != null -> {
                            // This is your Base64 string from the backend
                            Base64Image(
                                base64Data = existingImageUrl,
                                contentDescription = name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                } else {
                    // Show a simple icon if there's no image
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = "Default item icon",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Select Image")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Text Fields ---
            OutlinedTextField(value = name, onValueChange = { viewModel.name.value = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { viewModel.description.value = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = price, onValueChange = { viewModel.price.value = it }, label = { Text("Price") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = supply, onValueChange = { viewModel.supply.value = it }, label = { Text("Supply") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = keywords,
                onValueChange = { viewModel.keywords.value = it },
                label = { Text("Keywords (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Save Button ---
            Button(
                onClick = { viewModel.saveItem(restaurantId, context) },
                enabled = saveState !is SaveItemState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Item")
            }
        }
    }
}
