package com.example.apfront.ui.screens.restaurant_dashboard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
                FloatingActionButton(onClick = { navController.navigate("create_edit_item/$restaurantId") }) {
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
                            viewModel = viewModel,
                            navController = navController,
                            restaurantId = restaurantId
                        )
                        1 -> MenuCategoriesList(
                            menuData = state.menuData,
                            viewModel = viewModel
                        )
                    }
                }
                is MenuItemsUiState.Error -> {
                    Text("An error occurred. Code: ${state.code}")
                }
            }
        }
    }
}

@Composable
fun AllItemsList(
    menuData: VendorMenuResponse,
    viewModel: MenuItemsViewModel,
    navController: NavController,
    restaurantId: Int
) {
    val allItems by remember(menuData) {
        derivedStateOf {
            menuData.menu?.values?.flatten()?.distinctBy { it.id } ?: emptyList()
        }
    }

    if (allItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You haven't added any food items yet.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allItems) { item ->
                FoodItemCard(
                    item = item,
                    onEditClicked = { navController.navigate("create_edit_item/$restaurantId?itemId=${item.id}") },
                    onDeleteClicked = { viewModel.deleteFoodItem(item.id) }
                )
            }
        }
    }
}

@Composable
fun FoodItemCard(
    item: ItemDto,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Using your custom Base64Image composable
            Base64Image(
                base64Data = item.image,
                contentDescription = item.name,
                modifier = Modifier.size(120.dp)
            )
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("Price: ${item.price}", style = MaterialTheme.typography.bodySmall)
                Text("Supply: ${item.supply}", style = MaterialTheme.typography.bodySmall)
            }
            Column {
                TextButton(onClick = onEditClicked) { Text("Edit") }
                TextButton(onClick = onDeleteClicked) { Text("Delete") }
            }
        }
    }
}

@Composable
fun MenuCategoriesList(
    menuData: VendorMenuResponse,
    viewModel: MenuItemsViewModel
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryTitle by remember { mutableStateOf("") }
    var showAddItemDialog by remember { mutableStateOf<String?>(null) }

    val allItems = remember(menuData) {
        menuData.menu?.values?.flatten()?.distinctBy { it.id } ?: emptyList()
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("New Menu Category") },
            text = { OutlinedTextField(value = newCategoryTitle, onValueChange = { newCategoryTitle = it }, label = { Text("Category Title") }) },
            confirmButton = { Button(onClick = {
                viewModel.createMenuCategory(newCategoryTitle)
                showAddCategoryDialog = false
                newCategoryTitle = ""
            }) { Text("Create") } },
            dismissButton = { TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") } }
        )
    }

    showAddItemDialog?.let { menuTitle ->
        AddItemToMenuDialog(
            allItems = allItems,
            onDismiss = { showAddItemDialog = null },
            onItemSelected = { itemId ->
                viewModel.addItemToMenu(menuTitle, itemId)
                showAddItemDialog = null
            }
        )
    }

    Column {
        Button(onClick = { showAddCategoryDialog = true }, modifier = Modifier.padding(16.dp)) {
            Text("Add New Category")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuData.menuTitles) { title ->
                MenuCategoryCard(
                    title = title,
                    items = menuData.menu?.get(title) ?: emptyList(),
                    onDeleteCategoryClicked = { viewModel.deleteMenuCategory(title) },
                    onRemoveItemClicked = { itemId -> viewModel.removeItemFromMenu(title, itemId) },
                    onAddItemClicked = { showAddItemDialog = title }
                )
            }
        }
    }
}

@Composable
fun MenuCategoryCard(
    title: String,
    items: List<ItemDto>,
    onDeleteCategoryClicked: () -> Unit,
    onRemoveItemClicked: (Int) -> Unit,
    onAddItemClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = onDeleteCategoryClicked) { Icon(Icons.Default.Delete, contentDescription = "Delete Category") }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "Expand")
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onRemoveItemClicked(item.id) }) {
                                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Remove item from menu")
                            }
                        }
                    }
                    TextButton(onClick = onAddItemClicked, modifier = Modifier.align(Alignment.End)) {
                        Text("Add Item...")
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemToMenuDialog(
    allItems: List<ItemDto>,
    onDismiss: () -> Unit,
    onItemSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item to Menu") },
        text = {
            LazyColumn {
                items(allItems) { item ->
                    Text(
                        text = item.name,
                        modifier = Modifier.fillMaxWidth().clickable { onItemSelected(item.id) }.padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
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
                Log.d("Base64Image", "Raw base64 (start): ${it.take(100)}")

                val cleanBase64 = it
                    .substringAfter("base64,", it)
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace(" ", "")

                val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                Log.d("Base64Image", "Bitmap decode result: ${bitmap != null}")
                bitmap?.asImageBitmap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Base64Image", "Decode failed: ${e.message}")
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
