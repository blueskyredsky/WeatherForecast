package com.currentweather.ui

import android.location.Location
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
import com.currentweather.data.repository.WeatherRepository
import com.currentweather.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var hasFetchedCurrentWeather = false

    private val _currentWeather = MutableStateFlow<Result<CurrentWeather?>?>(null)
    val currentWeather = _currentWeather.asStateFlow()

    private val _hourlyForecast = MutableStateFlow<Result<Forecast?>?>(null)
    val hourlyForecast = _hourlyForecast.asStateFlow()

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


    /*private fun fetchWeatherOnLocation() {
        if (!hasFetchedCurrentWeather) {
            if (_currentWeather.value is Result.Loading) return

            _currentWeather.value = Result.Loading

            viewModelScope.launch {
                try {
                    // Collect the first location update and then cancel the flow
                    locationRepository.getLocationUpdates()
                        .first()
                        .let { location ->
                            fetchWeather(location)
                        }
                } catch (e: Exception) {
                    // Handle the case where the first location update fails
                    try {
                        locationRepository.getLastKnownLocation()?.let { lastLocation ->
                            fetchWeather(lastLocation)
                        } ?: run {
                            _currentWeather.value = Result.Error(
                                Exception("Could not get current or last known location. ${e.message}"),
                                ErrorType.UNKNOWN
                            )
                        }
                    } catch (e: Exception) {
                        _currentWeather.value = Result.Error(e, ErrorType.UNKNOWN)
                    }
                }
            }
            hasFetchedCurrentWeather = true
        }
    }*/



    private fun fetchWeatherOnLocation() {
        if (!hasFetchedCurrentWeather) {
            // Only proceed if we are not already fetching
            if (_currentWeather.value is Result.Loading || _hourlyForecast.value is Result.Loading) return

            // Set both states to loading
            _currentWeather.value = Result.Loading
            _hourlyForecast.value = Result.Loading

            viewModelScope.launch {
                try {
                    locationRepository.getLocationUpdates().first().let { location ->
                        val locationString = "${location.latitude},${location.longitude}"
                        val currentWeatherDeferred = async { weatherRepository.fetchCurrentWeather(locationString) }
                        val forecastDeferred = async { weatherRepository.fetchForecast(locationString, 1) }

                        // Await both results and handle them
                        val currentWeatherResult = currentWeatherDeferred.await()
                        val forecastResult = forecastDeferred.await()

                        handleCurrentWeatherResult(currentWeatherResult)
                        handleForecastResult(forecastResult)
                    }
                } catch (e: Exception) {
                    // ... (Existing error handling for location)
                }
            }
            hasFetchedCurrentWeather = true
        }
    }



    /**
     * Helper function to handle the result of the forecast API call.
     */
    private fun handleForecastResult(result: kotlin.Result<Forecast>) {
        result.onSuccess { forecast ->
            _hourlyForecast.value = Result.Success(forecast)
        }.onFailure { throwable ->
            val errorMessage: String = when (throwable) {
                is RepositoryError.NetworkError -> "Failed to fetch forecast. Please check your internet connection. (Error Code: ${throwable.code})"
                is RepositoryError.NoDataError -> "No forecast data was returned."
                is RepositoryError.MappingError -> "There was an issue processing the forecast data."
                is RepositoryError.UnknownError -> "An unexpected error occurred: ${throwable.message}"
                else -> "An unknown error has occurred."
            }
            _hourlyForecast.value = Result.Error(Exception(errorMessage))
        }
    }

    /**
     * Helper function to handle the result of the current weather API call.
     */
    private fun handleCurrentWeatherResult(result: kotlin.Result<CurrentWeather>) {
        result.onSuccess { weather ->
            _currentWeather.value = Result.Success(weather)
        }.onFailure { throwable ->
            val errorMessage: String = when (throwable) {
                is RepositoryError.NetworkError -> "Failed to fetch forecast. Please check your internet connection. (Error Code: ${throwable.code})"
                is RepositoryError.NoDataError -> "No forecast data was returned."
                is RepositoryError.MappingError -> "There was an issue processing the forecast data."
                is RepositoryError.UnknownError -> "An unexpected error occurred: ${throwable.message}"
                else -> "An unknown error has occurred."
            }
            _currentWeather.value = Result.Error(Exception(errorMessage))
        }
    }

    /**
     * Helper function to perform the actual API call once location is available.
     */
    private fun fetchWeather(location: Location) {
        viewModelScope.launch {
            weatherRepository.fetchCurrentWeather("${location.latitude},${location.longitude}")
                .onSuccess { weather ->
                    _currentWeather.value = Result.Success(weather)
                }
                .onFailure { throwable ->
                    val errorMessage: String = when (throwable) {
                        is RepositoryError.NetworkError -> "Failed to connect to the server. Please check your internet connection. (Error Code: ${throwable.code})"
                        is RepositoryError.NoDataError -> "No weather data was returned."
                        is RepositoryError.MappingError -> "There was an issue processing the weather data."
                        is RepositoryError.UnknownError -> "An unexpected error occurred: ${throwable.message}"
                        else -> "An unknown error has occurred."
                    }
                    _currentWeather.value = Result.Error(Exception(errorMessage))
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
        _currentWeather.value = null
        _hourlyForecast.value = null
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