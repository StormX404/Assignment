package com.example.assignment.presentation.cities

import CustomSearchBar
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment.common.CityRow
import kotlinx.coroutines.delay
import androidx.core.net.toUri

@SuppressLint("QueryPermissionsNeeded")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CitiesScreen(
    modifier: Modifier = Modifier,
    viewModel: CitiesViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current

    val cities by viewModel.cities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(query) {
        delay(400)
        viewModel.searchCities(query)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cities Search") }) },
        modifier = modifier
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            CustomSearchBar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Text(
                        text = "Error: ${error!!}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                cities.isEmpty() && query.isNotBlank() && !isLoading -> {
                    Text(
                        text = "No cities found for \"$query\".",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                cities.isEmpty() && query.isBlank() && !isLoading -> {
                    Text(
                        text = "Start typing in the search box to find cities.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    val groupedCities = cities.groupBy { it.name.firstOrNull()?.uppercaseChar() ?: '#' }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        groupedCities.forEach { (initial, group) ->
                            stickyHeader {
                                Text(
                                    text = initial.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }

                            items(group) { city ->
                                CityRow(
                                    city = city,
                                    onClick = {
                                        val gmmIntentUri = "geo:${city.latitude},${city.longitude}?q=${Uri.encode(city.name)}".toUri()
                                        val mapIntent =
                                            Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                                setPackage("com.google.android.apps.maps")
                                            }
                                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(mapIntent)
                                        } else {
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
