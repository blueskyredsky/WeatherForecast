package com.currentweather.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.common.model.ErrorType
import com.currentweather.R
import com.currentweather.ui.component.CustomSearchBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeatherScreen(
    onNavigateToDetail: (cityName: String) -> Unit,
    viewModel: CurrentWeatherViewModel
) {
    val weatherUIState by viewModel.weatherUIState.collectAsStateWithLifecycle()
    val weatherUIData by viewModel.weatherUIData.collectAsStateWithLifecycle()
    val searchLocation by viewModel.searchLocation.collectAsStateWithLifecycle()
    val searchLocationResults by viewModel.searchLocationResults.collectAsStateWithLifecycle()
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

    val appBarColor = colorResource(id = weatherUIData.backgroundColorResource)

    LaunchedEffect(Unit) {
        viewModel.startLocationWeatherUpdates()
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

    LaunchedEffect(Unit) {
        viewModel.observeSearchLocation()
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(appBarColor)) {
                CustomSearchBar(
                    query = searchLocation,
                    onQueryChange = viewModel::updateSearchLocation,
                    onSearch = viewModel::searchLocationApiCall,
                    searchResults = searchLocationResults,
                    onResultClick = {
                        viewModel.handleSearchResultClick(it)
                    }
                )
            }
        },
        snackbarHost = {
            // todo to be completed
        }
    ) { innerPadding ->
        when (val result = weatherUIState) {
            is WeatherUIState.Error -> {
                when (result.errorType) {
                    ErrorType.LocationServicesDisabled -> {
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

                    ErrorType.LocationPermissionDenied -> {
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

                    ErrorType.UnknownError -> {
                        Button(onClick = {
                            viewModel.retryFetchWeather()
                        }) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.retry)
                            )
                        }
                    }

                    ErrorType.MappingError -> {
                        // todo
                    }

                    ErrorType.NetworkError -> {
                        // todo
                    }

                    ErrorType.NoDataError -> {
                        // todo
                    }
                }
            }

            WeatherUIState.Idle -> Unit
            WeatherUIState.Loading -> {
                LoadingContent(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .fillMaxSize()
                )
            }

            is WeatherUIState.Success ->
                SuccessContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    weatherData = weatherUIData,
                    forecast = result.forecast,
                    currentWeather = result.currentWeather,
                    onNavigateToDetail = onNavigateToDetail
                )
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