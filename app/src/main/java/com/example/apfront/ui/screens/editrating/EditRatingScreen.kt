package com.example.apfront.ui.screens.editrating

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.ui.components.StarRatingSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRatingScreen(
    navController: NavController,
    viewModel: EditRatingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    // This block will run once when the initial rating data is loaded
    LaunchedEffect(uiState.rating) {
        uiState.rating?.let {
            rating = it.rating
            comment = it.comment
        }
    }

    // This block handles navigation after a successful update or delete
    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Edit Your Review") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
                        label = { Text("Edit your comment") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.updateRating(rating, comment) },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        else Text("Save Changes")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.deleteRating() },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        if (uiState.isDeleting) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        else Text("Delete Review")
                    }
                }
            }
        }
    }
}
