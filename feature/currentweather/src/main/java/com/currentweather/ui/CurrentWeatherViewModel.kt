package com.currentweather.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.common.R
import com.common.model.ErrorType
import com.common.model.Result
import com.common.model.error.RepositoryError
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.model.forecast.Forecast
import com.currentweather.data.repository.LocationRepository
import com.currentweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getOrThrow
import kotlin.onFailure

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var hasFetchedCurrentWeather = false

    private val _weatherUIState = MutableStateFlow(WeatherUIState(isLoading = true))
    val weatherUIState = _weatherUIState.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted = _locationPermissionGranted.asStateFlow()

    private val _locationEnabled = MutableStateFlow(false)
    val locationEnabled = _locationEnabled.asStateFlow()

    private val _requestLocationPermissions = MutableStateFlow(false)
    val requestLocationPermissions = _requestLocationPermissions.asStateFlow()

    fun checkLocationPermission() {
        _locationPermissionGranted.value = locationRepository.hasLocationPermissions()
        if (!locationPermissionGranted.value) {
            _requestLocationPermissions.value = true
            _weatherUIState.value = WeatherUIState(isLoading = false)
            _currentWeather.value = Result.Error(Exception("Location permissions denied."), ErrorType.LOCATION_PERMISSIONS_DENIED)
        }
    }

    fun startObservingLocationAndWeather() {
        locationRepository.isLocationEnabled()
            .onEach { isEnabled ->
                if (!isEnabled) {
                    _currentWeather.value = Result.Error(Exception("Location services are disabled."), ErrorType.LOCATION_SERVICES_DISABLED)
                }
                _locationEnabled.value = isEnabled
            }
            .launchIn(viewModelScope)

        combine(locationPermissionGranted, locationEnabled) { permissionGranted, locationEnabled ->
            permissionGranted && locationEnabled
        }.onEach { shouldFetch ->
            if (shouldFetch) {
                fetchWeatherOnLocation()
            }
        }.launchIn(viewModelScope)

        checkLocationPermission()
    }

    @Suppress("UNCHECKED_CAST")
    private fun fetchWeatherOnLocation() {
        if (_weatherUIState.value.isLoading) return

        _weatherUIState.value = WeatherUIState(isLoading = true)

        viewModelScope.launch {
            try {
                locationRepository.getLocationUpdates().first().let { location ->
                    val locationString = "${location.latitude},${location.longitude}"

                    val (currentWeatherResult, forecastResult) = awaitAll(
                        async { weatherRepository.fetchCurrentWeather(locationString) },
                        async { weatherRepository.fetchForecast(locationString, 1) }
                    )

                    val currentWeather = (currentWeatherResult as? kotlin.Result<CurrentWeather>)?.getOrThrow()
                    val forecast = (forecastResult as? kotlin.Result<Forecast>)?.getOrThrow()

                    // Update the UI state with success data
                    _weatherUIState.value = WeatherUIState(
                        currentWeather = currentWeather,
                        hourlyForecast = forecast,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                val errorType = when (e) {
                    is RepositoryError.NetworkError -> ErrorType.NetworkError
                    is RepositoryError.NoDataError -> ErrorType.NoDataError
                    is RepositoryError.MappingError -> ErrorType.MappingError
                    else -> ErrorType.UnknownError
                }
                _weatherUIState.value = WeatherUIState(errorType = errorType)
            }
        }
    }

    fun onLocationPermissionsGranted() {
        _locationPermissionGranted.value = true
        _requestLocationPermissions.value = false
    }

    fun permissionRequestHandled() {
        _requestLocationPermissions.value = false
    }

    fun retryFetchWeather() {
        _weatherUIState.value = null
        hasFetchedCurrentWeather = false
        fetchWeatherOnLocation()
    }

    fun getWeatherUI(text: String): WeatherUI {
        return when {
            text.lowercase().contains("sun") -> WeatherUI(
                backgroundImageResource = R.drawable.ic_sunny,
                backgroundColorResource = R.color.light_beige
            )

            text.lowercase().contains("cloud") || text.lowercase().contains("wind") -> WeatherUI(
                backgroundImageResource = R.drawable.ic_cloudy,
                backgroundColorResource = R.color.pale_blue
            )

            text.lowercase().contains("rain") || text.lowercase()
                .contains("drizzle") || text.lowercase()
                .contains("snow") -> WeatherUI(
                backgroundImageResource = R.drawable.ic_rainy,
                backgroundColorResource = R.color.dark_shade_blue,
                textColorResource = R.color.white
            )

            else -> WeatherUI(R.drawable.ic_cloudy, R.color.pale_blue)
        }
    }
}

data class WeatherUI(
    @DrawableRes val backgroundImageResource: Int,
    @ColorRes val backgroundColorResource: Int,
    @ColorRes val textColorResource: Int = R.color.black
)

data class WeatherUIState(
    val currentWeather: CurrentWeather? = null,
    val hourlyForecast: Forecast? = null,
    val isLoading: Boolean = false,
    val errorType: ErrorType? = null,
)