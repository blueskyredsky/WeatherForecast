package com.currentweather.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
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
    viewModel: CurrentWeatherViewModel = hiltViewModel()
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

    // to fetch weather when the app is in the foreground
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.startObservingLocationAndWeather()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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

    /*when (val result = currentWeather) {
        is Result.Error -> {

        }
        Result.Loading -> LoadingContent(modifier = Modifier.fillMaxSize())

        is Result.Success -> {

        }
        null -> Unit
    }*/

    Scaffold(snackbarHost = {
        // todo to be completed
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.today),
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val result = currentWeather) {
                null -> Unit

                Result.Loading -> LoadingContent(modifier = Modifier.fillMaxSize())

                is Result.Success -> {
                    val weather = result.data
                    if (weather != null) {
                        Text(
                            text = "${weather.current?.tempC ?: ""}°",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        weather.current?.condition?.icon?.let { iconUrl ->
                            AsyncImage(
                                model = "https:${iconUrl}",
                                contentDescription = stringResource(R.string.weather_condition_icon),
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(
                                R.string.condition,
                                weather.current?.condition?.text ?: ""
                            )
                        )

                        Button(
                            onClick = {
                                weather.location?.name?.let {
                                    onNavigateToDetail(weather.location.name)
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(
                                    R.string.view_detail,
                                    weather.location?.name ?: ""
                                )
                            )
                        }

                    } else {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.no_weather_data_available)
                        )
                    }
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
            }

            Button(
                onClick = { viewModel.retryFetchWeather() },
                modifier = Modifier.padding(top = 16.dp),
                enabled = locationPermissionGranted && locationEnabled
            ) {
                Text(stringResource(R.string.fetch_weather))
            }
        }
    }
}

@Composable
fun LoadingContent(modifier: Modifier = Modifier) {
    val backgroundColor = Color(0xFFEBF4FA)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Placeholder for the "Today" text
            Spacer(modifier = Modifier.height(56.dp))
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(20.dp)
                    .shimmerEffect()
            )

            // Placeholder for the large temperature number
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .size(150.dp, 200.dp)
                    .shimmerEffect()
            )

            // Placeholder for the location text
            Spacer(modifier = Modifier.height(60.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .shimmerEffect()
            )

            // Placeholder for the horizontal wave line
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .shimmerEffect()
            )

            // Placeholder for the hourly forecast items
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(6) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(12.dp)
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorContent(modifier: Modifier = Modifier) {

}

@Composable
fun CurrentWeatherContent(modifier: Modifier = Modifier) {

}

fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "shimmerAnimation"
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value, y = 0f),
            end = Offset(x = translateAnimation.value + 500f, y = 500f)
        )
    )
}