package com.example.apfront.ui.screens.submitrating

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.apfront.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitRatingScreen(
    navController: NavController,
    viewModel: SubmitRatingViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.submissionState) {
        if (uiState.submissionState is Resource.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.submit_rating_title)) }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.how_was_your_order_prompt), style = MaterialTheme.typography.headlineSmall)
            StarRatingSelector(
                rating = rating,
                onRatingChanged = { newRating -> rating = newRating }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text(stringResource(R.string.leave_a_comment_label)) },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.submitRating(rating, comment) },
                enabled = !uiState.isSubmitting && rating > 0,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text(stringResource(R.string.submit_review_button))
            }
        }
    }
}