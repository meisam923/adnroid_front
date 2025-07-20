package com.example.apfront.ui.screens.restaurant_dashboard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfront.data.remote.dto.CreateItemRequest
import com.example.apfront.data.remote.dto.FoodItemDto // You'll need this for the loadItem function
import com.example.apfront.data.repository.RestaurantRepository
import com.example.apfront.util.Resource
import com.example.apfront.util.SessionManager
import com.example.apfront.util.uriToBase64
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// This sealed interface defines all possible states for the save operation.
// The UI will watch this to know when to show a loading spinner, a success message, etc.
sealed interface SaveItemState {
    object Idle : SaveItemState
    object Loading : SaveItemState
    object Success : SaveItemState
    data class Error(val code: Int?) : SaveItemState
}

@HiltViewModel
class CreateEditItemViewModel @Inject constructor(
    // Injected dependencies provided by Hilt
    private val repository: RestaurantRepository,
    private val sessionManager: SessionManager,
    private val savedStateHandle: SavedStateHandle // Used to get navigation arguments like itemId
) : ViewModel() {

    // --- State Variables for the Form ---
    // These MutableStateFlows hold the current text in each TextField.
    // The UI observes these to display the text.
    val name = MutableStateFlow("")
    val description = MutableStateFlow("")
    val price = MutableStateFlow("")
    val supply = MutableStateFlow("")
    val keywords = MutableStateFlow("")
    val imageUri = MutableStateFlow<Uri?>(null) // Holds the Uri of a newly selected image
    val existingImageUrl = MutableStateFlow<String?>(null) // Holds the existing image from the server when editing

    // This StateFlow tells the UI the current status of the save operation.
    private val _saveState = MutableStateFlow<SaveItemState>(SaveItemState.Idle)
    val saveState = _saveState.asStateFlow()

    // This checks if an "itemId" was passed to this screen during navigation.
    // If it's null, we are in "Add" mode. If it has a value, we are in "Edit" mode.
    private val itemId: Int? = savedStateHandle.get<Int>("itemId")?.takeIf { it != -1 }

    // --- Functions (Actions) ---

    // This function is called from the UI when in "Edit" mode to pre-fill the form.
    fun loadItemForEditing(restaurantId: Int) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty() || itemId == null) return

        viewModelScope.launch {
            val result = repository.getVendorMenu(token, restaurantId)
            if (result is Resource.Success) {
                val allItems = result.data?.menu?.values?.flatten() ?: emptyList()
                val itemToEdit = allItems.find { it.id == itemId }

                itemToEdit?.let {
                    name.value = it.name
                    description.value = it.description
                    price.value = it.price.toString()
                    supply.value = it.supply.toString()
                    keywords.value = it.keywords.joinToString(", ")
                    existingImageUrl.value = it.image
                }
            }
        }
    }

    // This function is called when the user clicks the "Save" button in the UI.
    fun saveItem(restaurantId: Int, context: Context) {
        val token = sessionManager.getAuthToken()
        if (token.isNullOrEmpty()) { return }

        viewModelScope.launch {
            // 1. Tell the UI we are now loading.
            _saveState.value = SaveItemState.Loading

            // 2. Prepare the data for the API call.
            val newImageBase64 = imageUri.value?.let { uriToBase64(context, it) }
            val keywordsList = keywords.value.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val request = CreateItemRequest(
                name = name.value,
                description = description.value,
                price = price.value.toLongOrNull() ?: 0L,
                supply = supply.value.toIntOrNull() ?: 0,
                image = newImageBase64 ?: existingImageUrl.value,
                keywords = keywordsList
            )

            // 3. Decide whether to call the "add" or "update" function in the repository.
            val result = if (itemId == null) {
                repository.addFoodItem(token, restaurantId, request)
            } else {
                repository.updateFoodItem(token, restaurantId, itemId, request)
            }

            // 4. Update the UI state with the final result (Success or Error).
            when (result) {
                is Resource.Success -> _saveState.value = SaveItemState.Success
                is Resource.Error -> _saveState.value = SaveItemState.Error(result.code)
                else -> {}
            }
        }
    }
}
