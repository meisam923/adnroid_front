package com.example.apfront.ui.screens.editrating

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
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

    LaunchedEffect(uiState.rating) {
        uiState.rating?.let {
            rating = it.rating
            comment = it.comment
        }
    }

    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_review_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.go_back_description))
                    }
                }
            )
        }
    ) { padding ->
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
                    StarRatingSelector(rating = rating, onRatingChanged = { newRating -> rating = newRating })
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text(stringResource(R.string.edit_comment_label)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.updateRating(rating, comment) },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        else Text(stringResource(R.string.save_changes_button))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.deleteRating() },
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        if (uiState.isDeleting) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        else Text(stringResource(R.string.delete_review_button))
                    }
                }
            }
        }
    }
}