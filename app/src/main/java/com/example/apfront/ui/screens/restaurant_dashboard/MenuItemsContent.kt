package com.example.apfront.ui.screens.restaurant_dashboard

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.apfront.R
import com.example.apfront.data.remote.dto.ItemDto
import com.example.apfront.data.remote.dto.VendorMenuResponse
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun MenuItemsContent(
    restaurantId: Int,
    navController: NavController,
    viewModel: MenuItemsViewModel = hiltViewModel()
) {
    var selectedSubTabIndex by remember { mutableStateOf(0) }
    val subTabs = listOf(
        stringResource(id = R.string.tab_all_items),
        stringResource(id = R.string.tab_menu_categories)
    )
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = restaurantId) {
        viewModel.loadMenu(restaurantId)
    }

    Scaffold(
        floatingActionButton = {
            if (selectedSubTabIndex == 0) {
                FloatingActionButton(onClick = {navController.navigate("create_edit_item/$restaurantId")}) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_new_item))
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedSubTabIndex) {
                subTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTabIndex == index,
                        onClick = { selectedSubTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (val state = uiState) {
                is MenuItemsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MenuItemsUiState.Success -> {
                    when (selectedSubTabIndex) {
                        0 -> AllItemsList(
                            menuData = state.menuData,
                            viewModel = viewModel, // <-- Pass the viewModel
                            navController = navController, // <-- Pass the navController
                            restaurantId = restaurantId)
                        1 -> MenuCategoriesList(menuData = state.menuData)
                    }
                }
                is MenuItemsUiState.Error -> {
                    Text("An error occurred. Code: ${state.code}")
                }
            }
        }
    }
}

// --- This Composable is now fully implemented ---
@Composable
fun AllItemsList(
    menuData: VendorMenuResponse,
    viewModel: MenuItemsViewModel, // <-- Accepts the viewModel
    navController: NavController, // <-- Accepts the navController
    restaurantId: Int
) {
    // This logic gets a flat, unique list of items from the nested API response
    val allItems by remember(menuData) {
        derivedStateOf {
            menuData.menu?.values?.flatten()?.distinctBy { it.id } ?: emptyList()}
    }

    if (allItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You haven't added any food items yet.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allItems) { item ->
                FoodItemCard(
                    item = item,
                    onEditClicked = {navController.navigate("create_edit_item/$restaurantId?itemId=${item.id}")
                    },
                    // The Delete button now calls the ViewModel's function
                    onDeleteClicked = { viewModel.deleteFoodItem(item.id) }
                )
            }
        }
    }
}

// --- This Composable is used by the list above ---
@Composable
fun FoodItemCard(
    item: ItemDto,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().height(120.dp)) { // Increased height
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Image with Placeholder ---
            Base64Image(
                base64Data = item.image,
                contentDescription = item.name,
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop
            )
            // --- Details Column ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Price: ${item.price}", style = MaterialTheme.typography.bodySmall)
                // --- Supply Count ---
                Text(text = "Supply: ${item.supply}", style = MaterialTheme.typography.bodySmall)
            }

            // --- Buttons Column ---
            Column {
                TextButton(onClick = onEditClicked) {
                    Text("Edit")
                }
                TextButton(onClick = onDeleteClicked) {
                    Text("Delete")
                }
            }
        }
    }}
// --- This is still a placeholder ---
@Composable
fun MenuCategoriesList(menuData: VendorMenuResponse) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("List of menu categories will be here.")
    }
}
@Composable
fun Base64Image(
    base64Data: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imageBitmap: ImageBitmap? = remember(base64Data) {
        try {
            base64Data?.let {
                val imageBytes = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.ic_placeholder),
            contentDescription = "Placeholder",
            modifier = modifier,
            contentScale = contentScale
        )
    }
}