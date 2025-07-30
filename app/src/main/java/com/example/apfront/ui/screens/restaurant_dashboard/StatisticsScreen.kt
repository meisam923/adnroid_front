package com.example.apfront.ui.screens.restaurant_dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apfront.R
import com.example.apfront.util.Resource


@Composable
    fun StatisticsScreen(
        viewModel: StatisticsViewModel = hiltViewModel(),
        restaurantId: Int
    ) {
        val statisticsResource by viewModel.statistics.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.fetchStatistics(restaurantId)
        }

        when (val resource = statisticsResource) {
            is Resource.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
                val list = resource.data ?: emptyList()
                val totalIncome = list.sumOf { it.income.toDouble() }

                val pieData = list.map {
                    "${it.year}/${it.month}" to it.income.toFloat()
                }

                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.total_income, totalIncome),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn {
                        items(list) { stat ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("${stringResource(id = R.string.month)}: ${stat.year}/${stat.month}", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(4.dp))
                                    Text("${stringResource(id = R.string.income)}: ${stat.income}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            is Resource.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.generic_error), color = Color.Red)
                }
            }

            is Resource.Idle<*> -> TODO()
        }
    }

