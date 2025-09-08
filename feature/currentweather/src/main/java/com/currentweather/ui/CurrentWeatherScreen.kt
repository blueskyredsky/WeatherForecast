package com.currentweather.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.common.model.ErrorType
import com.common.model.Result
import com.currentweather.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CurrentWeatherScreen(
    onNavigateToDetail: (cityName: String) -> Unit,
    viewModel: CurrentWeatherViewModel
) {
    val currentWeather by viewModel.currentWeather.collectAsStateWithLifecycle()
    val hourlyForecast by viewModel.hourlyForecast.collectAsStateWithLifecycle()
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
        viewModel.startObservingLocationAndWeather()
    }

    LaunchedEffect(requestLocationPermissions) {
        if (requestLocationPermissions) {
            locationPermissionsState.launchMultiplePermissionRequest()
            viewModel.permissionRequestHandled()
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.onLocationPermissionsGranted()
        }
    }

    Scaffold(
        snackbarHost = {
            // todo to be completed
        }
    ) { innerPadding ->
        when (val result = currentWeather) {
            Result.Loading -> LoadingContent(modifier = Modifier.fillMaxSize())

            is Result.Success -> {
                SuccessContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    weatherData = viewModel.getWeatherUI(
                        result.data?.current?.condition?.text ?: ""
                    ),
                    currentWeather = result.data,
                    onNavigateToDetail = onNavigateToDetail
                )
            }

            is Result.Error -> {
                    val errorMessage =
                        result.exception.message ?: stringResource(R.string.an_unknown_error_occurred)
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.error, errorMessage),
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.height(8.dp))

                    when (result.errorType) {
                        ErrorType.LOCATION_SERVICES_DISABLED -> {
                            Button(onClick = {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context.startActivity(intent)
                            }) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = stringResource(R.string.enable_location_services)
                                )
                            }
                        }

                        ErrorType.LOCATION_PERMISSIONS_DENIED -> {
                            val showRationale =
                                locationPermissionsState.permissions.any { it.status.shouldShowRationale }
                            val permanentlyDenied = !locationPermissionsState.allPermissionsGranted &&
                                    !locationPermissionsState.permissions.any { it.status.shouldShowRationale }

                            if (permanentlyDenied) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = stringResource(R.string.location_permission_permanently_denied_please_enable_in_settings)
                                )
                                Button(onClick = {
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                    context.startActivity(intent)
                                }) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.open_settings)
                                    )
                                }
                            } else if (showRationale) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = stringResource(R.string.location_permission_is_important_for_this_app_please_grant_it)
                                )
                                Button(onClick = {
                                    locationPermissionsState.launchMultiplePermissionRequest()
                                }) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.request_permission_again)
                                    )
                                }
                            } else {
                                Button(onClick = {
                                    locationPermissionsState.launchMultiplePermissionRequest()
                                }) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.grant_location_permissions)
                                    )
                                }
                            }
                        }

                        ErrorType.UNKNOWN -> {
                            Button(onClick = {
                                viewModel.retryFetchWeather()
                            }) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = stringResource(R.string.retry)
                                )
                            }
                        }
                    }
                }

                null -> Unit
            }

            // this button is manually fetch weather
            /*Button(
                onClick = { viewModel.retryFetchWeather() },
                modifier = Modifier.padding(top = 16.dp),
                enabled = locationPermissionGranted && locationEnabled
            ) {
                Text(stringResource(R.string.fetch_weather))
            }*/
    }
}