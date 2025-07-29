package com.example.apfront.ui.screens.admin_hub

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditCouponScreen(
    navController: NavController,
    viewModel: CreateEditCouponViewModel = hiltViewModel()
) {
    val couponCode by viewModel.couponCode.collectAsState()
    val type by viewModel.type.collectAsState() // <-- State for coupon type
    val value by viewModel.value.collectAsState()
    val minPrice by viewModel.minPrice.collectAsState()
    val userCount by viewModel.userCount.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val context = LocalContext.current

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    LaunchedEffect(saveState) {
        if (saveState is SaveCouponState.Success) {
            Toast.makeText(context, "Coupon saved!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create/Edit Coupon") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = couponCode, onValueChange = { viewModel.couponCode.value = it }, label = { Text("Coupon Code") }, modifier = Modifier.fillMaxWidth())

            // --- NEW: Radio buttons for CouponType ---
            Text("Coupon Type", style = MaterialTheme.typography.labelLarge)
            Row {
                Row(
                    Modifier.selectable(
                        selected = (type == "PERCENT"),
                        onClick = { viewModel.type.value = "PERCENT" }
                    ).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (type == "PERCENT"), onClick = { viewModel.type.value = "PERCENT" })
                    Text("Percent")
                }
                Row(
                    Modifier.selectable(
                        selected = (type == "FIXED"),
                        onClick = { viewModel.type.value = "FIXED" }
                    ).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (type == "FIXED"), onClick = { viewModel.type.value = "FIXED" })
                    Text("Fixed")
                }
            }

            OutlinedTextField(value = value, onValueChange = { viewModel.value.value = it }, label = { Text("Value (e.g., 10 or 10.0)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = minPrice, onValueChange = { viewModel.minPrice.value = it }, label = { Text("Minimum Price") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = userCount, onValueChange = { viewModel.userCount.value = it }, label = { Text("Usage Limit") }, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(startDate?.format(dateFormatter) ?: "Select Start Date")
                }
                OutlinedButton(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(endDate?.format(dateFormatter) ?: "Select End Date")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.saveCoupon() },
                enabled = saveState !is SaveCouponState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Coupon")
            }
        }
    }
if (showStartDatePicker) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = { showStartDatePicker = false },
        confirmButton = {
            TextButton(onClick = {
                viewModel.onStartDateSelected(datePickerState.selectedDateMillis)
                showStartDatePicker = false
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

if (showEndDatePicker) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = { showEndDatePicker = false },
        confirmButton = {
            TextButton(onClick = {
                viewModel.onEndDateSelected(datePickerState.selectedDateMillis)
                showEndDatePicker = false
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
}