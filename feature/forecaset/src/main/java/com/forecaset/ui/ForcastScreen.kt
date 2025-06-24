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
import androidx.compose.ui.res.stringResource
import com.common.model.ErrorType
import com.forecaset.R

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

    LaunchedEffect(key1 = locationPermissionGranted, key2 = locationEnabled) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.forecast_home_screen),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val result = currentWeather) {
            null -> {
                Text(stringResource(R.string.waiting_for_weather_data))
            }

            Result.Loading -> {
                CircularProgressIndicator()
                Text(stringResource(R.string.fetching_weather))
            }

            is Result.Success -> {
                val weather = result.data
                if (weather != null) {
                    Text(stringResource(R.string.current_weather))
                    Text(stringResource(R.string.location, weather.location?.name ?: ""))
                    Text(stringResource(R.string.temperature_c, weather.current?.tempC ?: ""))
                    Text(stringResource(R.string.condition, weather.current?.condition?.text ?: ""))
                    Button(
                        onClick = {
                            // todo handle navigation to detail screen
                            /*onNavigateToDetail()*/
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.view_detail, weather.location?.name ?: ""))
                    }
                } else {
                    Text(stringResource(R.string.no_weather_data_available))
                }
            }

            is Result.Error -> {
                val errorMessage =
                    result.exception.message ?: stringResource(R.string.an_unknown_error_occurred)
                Text(
                    text = stringResource(R.string.error, errorMessage),
                    color = MaterialTheme.colorScheme.error
                )

                when (result.errorType) {
                    ErrorType.LOCATION_SERVICES_DISABLED -> {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            context.startActivity(intent)
                        }) {
                            Text(stringResource(R.string.enable_location_services))
                        }
                    }
                    ErrorType.LOCATION_PERMISSIONS_DENIED -> {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }) {
                            Text(stringResource(R.string.grant_location_permissions))
                        }
                    }
                    ErrorType.UNKNOWN -> {
                        Button(onClick = {
                            if (locationPermissionGranted && locationEnabled) {
                                viewModel.fetchWeatherOnLocation()
                            }
                        }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }

                // Or if rationale should be shown
                locationPermissionsState.permissions.forEach { permissionState ->
                    if (permissionState.status.shouldShowRationale) {
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.location_permission_is_important_for_this_app_please_grant_it))
                        Button(onClick = {
                            permissionState.launchPermissionRequest()
                        }) {
                            Text(stringResource(R.string.request_permission_again))
                        }
                    }
                }
            }
        }

        // Button to manually fetch weather
        Button(
            onClick = { viewModel.fetchWeatherOnLocation() },
            modifier = Modifier.padding(top = 16.dp),
            enabled = locationPermissionGranted && locationEnabled // Enable only if conditions are met
        ) {
            Text(stringResource(R.string.fetch_weather))
        }
    }
}