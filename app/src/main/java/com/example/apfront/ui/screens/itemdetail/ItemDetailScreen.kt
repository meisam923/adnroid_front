package com.example.apfront.ui.screens.itemdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavController,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(uiState.item?.name ?: "Loading...") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            } else if (uiState.item != null) {
                val item = uiState.item!!
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // TODO: Add a large image of the food item here
                    Text(item.name, style = MaterialTheme.typography.headlineMedium)
                    Text(item.description, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))
                    Text("$${item.price}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { viewModel.onRemoveItem() }, enabled = uiState.quantityInCart > 0) {
                            Icon(Icons.Default.Remove, contentDescription = "Remove one")
                        }
                        Text("${uiState.quantityInCart}", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 16.dp))
                        IconButton(onClick = { viewModel.onAddItem() }) {
                            Icon(Icons.Default.Add, contentDescription = "Add one")
                        }
                    }
                }
            }
        }
    }
}