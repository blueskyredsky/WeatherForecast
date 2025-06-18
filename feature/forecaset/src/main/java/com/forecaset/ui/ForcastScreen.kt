package com.forecaset.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.first

@Composable
fun ForecastScreen(
    onNavigateToDetail: (locationId: String) -> Unit,
    viewModel: ForecastViewModel = hiltViewModel()
) {
    val hasLocationPermissions by viewModel.isLocationEnabled().collectAsState(initial = false)
    val currentWeatherResult by viewModel.currentWeather.collectAsState()

    // Launcher for requesting location permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // Permissions granted, try to load weather
            //viewModel.loadCurrentWeather()
        } else {
            // Permissions denied, show a message or direct to settings
            // You might want to show a SnackBar or AlertDialog here
            println("Location permissions denied.")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isLocationEnabled().first().let { isEnabled ->
            if (!isEnabled) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    LaunchedEffect(hasLocationPermissions) {
        if (hasLocationPermissions) {
            // get the current weather
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forecast Home Screen")
        Button(
            onClick = { onNavigateToDetail("London") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("View London Weather Detail")
        }
    }
}