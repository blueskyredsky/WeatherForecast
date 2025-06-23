package com.forecaset.ui

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.common.model.Result
import com.google.accompanist.permissions.shouldShowRationale
import android.provider.Settings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ForecastScreen(
    onNavigateToDetail: (locationId: String) -> Unit,
    viewModel: ForecastViewModel = hiltViewModel()
) {
    val currentWeather by viewModel.currentWeather.collectAsStateWithLifecycle()
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsStateWithLifecycle()
    val locationEnabled by viewModel.locationEnabled.collectAsStateWithLifecycle()
    val requestLocationPermissions by viewModel.requestLocationPermissions.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        viewModel.checkLocationPermission()
        viewModel.checkLocationServiceStatus()
        // Start fetching weather if permissions are already granted and location is enabled
        if (locationPermissionGranted && locationEnabled) {
            viewModel.fetchWeatherOnLocation()
        }
    }

    LaunchedEffect(requestLocationPermissions) {
        if (requestLocationPermissions) {
            // Launch permission request if ViewModel asks for it
            locationPermissionsState.launchMultiplePermissionRequest()
            viewModel.permissionRequestHandled()
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.onLocationPermissionsGranted()
        } else {
            if (locationPermissionsState.shouldShowRationale ||
                !locationPermissionsState.allPermissionsGranted &&
                locationPermissionsState.permissions.any { !it.status.isGranted }
            ) {
                viewModel.onLocationPermissionsDenied()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forecast Home Screen", modifier = Modifier.padding(bottom = 16.dp))

        when (val result = currentWeather) {
            null -> {
                Text("Waiting for weather data...")
            }
            Result.Loading -> {
                CircularProgressIndicator()
                Text("Fetching weather...")
            }
            is Result.Success -> {
                val weather = result.data
                if (weather != null) {
                    Text("Current Weather:")
                    Text("Location: ${weather.location?.name}")
                    Text("Temperature: ${weather.current?.tempC}°C")
                    Text("Condition: ${weather.current?.condition}")
                    Button(
                        onClick = { /*onNavigateToDetail()*/ },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("View ${weather.location?.name} Detail")
                    }
                } else {
                    Text("No weather data available. Please try again.")
                }
            }
            is Result.Error -> {
                val errorMessage = result.exception.message ?: "An unknown error occurred."
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)

                // Offer to enable location services if that's the error
                if (errorMessage.contains("Location services are disabled", ignoreCase = true)) {
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }) {
                        Text("Enable Location Services")
                    }
                }

                // Offer to grant permissions if that's the error and not already requested
                if (errorMessage.contains("Location permissions denied", ignoreCase = true) && !locationPermissionsState.allPermissionsGranted) {
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }) {
                        Text("Grant Location Permissions")
                    }
                }

                // Or if rationale should be shown
                locationPermissionsState.permissions.forEach { permissionState ->
                    if (permissionState.status.shouldShowRationale) {
                        Spacer(Modifier.height(8.dp))
                        Text("Location permission is important for this app. Please grant it.")
                        Button(onClick = {
                            permissionState.launchPermissionRequest()
                        }) {
                            Text("Request Permission Again")
                        }
                    }
                }
            }
        }

        // Button to manually fetch weather (useful for testing or re-fetching)
        Button(
            onClick = { viewModel.fetchWeatherOnLocation() },
            modifier = Modifier.padding(top = 16.dp),
            enabled = locationPermissionGranted && locationEnabled // Enable only if conditions are met
        ) {
            Text("Fetch Weather (Current Location)")
        }

        // Example for navigating to a hardcoded location (original button)
        Button(
            onClick = { onNavigateToDetail("London") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("View London Weather Detail")
        }
    }
}