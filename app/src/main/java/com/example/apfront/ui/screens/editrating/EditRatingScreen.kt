package com.example.apfront.ui.screens.editrating

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.ui.components.StarRatingSelector
// Import the constant from OrderDetailScreen or a shared constants file

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRatingScreen(
    navController: NavController,
    viewModel: EditRatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    // Keeps local UI state (rating/comment inputs) in sync with loaded rating details
    LaunchedEffect(uiState.rating) {
        uiState.rating?.let {
            rating = it.rating
            comment = it.comment
        }
    }

    // Handles navigation and setting result on successful operation (update or delete)
    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            // Set the result for the previous screen (OrderDetailScreen)
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("review_changed", true) // <--- THIS IS THE CORRECTED LINE
            navController.popBackStack()
            // Optional: Reset operationSuccess in ViewModel to prevent re-triggering
            // if this screen somehow recomposes without being destroyed.
            // viewModel.resetOperationStatus() // You'd need to add this method to ViewModel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_review_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Standard back navigation
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null && !uiState.operationSuccess) { // Show error only if not also successful
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            } else if (uiState.rating != null) { // Ensure rating data is present before showing edit UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StarRatingSelector(
                        rating = rating,
                        onRatingChanged = { newRating -> rating = newRating }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text(stringResource(R.string.edit_comment_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp) // Consider using minHeight and default text style for dynamic height
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Pushes buttons to the bottom

                    // Save Changes Button
                    Button(
                        onClick = { viewModel.updateRating(rating, comment) },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(R.string.save_changes_button))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Delete Review Button
                    OutlinedButton(
                        onClick = { viewModel.deleteRating() },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(R.string.delete_review_button))
                        }
                    }
                }
            } else if (!uiState.isLoading) { // Fallback if not loading, no error, but no rating data
                Text("Rating details not available.")
            }
        }
    }
}
