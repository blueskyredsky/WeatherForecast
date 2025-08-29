package com.currentweather.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.common.R
import com.common.model.ErrorType
import com.common.model.Result
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.repository.WeatherRepository
import com.currentweather.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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


    private fun fetchWeatherOnLocation() {
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
                                Exception("Could not get current or last known location."),
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
    }

    /**
     * Helper function to perform the actual API call once location is available.
     */
    private fun fetchWeather(location: Location) {
        viewModelScope.launch {
            try {
                val weather = weatherRepository.fetchCurrentWeather(
                    "${location.latitude},${location.longitude}"
                )
                _currentWeather.value = Result.Success(weather.getOrNull())
            } catch (e: Exception) {
                _currentWeather.value = Result.Error(e)
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
        fetchWeatherOnLocation()
    }

    fun getWeatherUI(text: String): WeatherUI {
        return when (text.lowercase()) {
            "sunny" -> WeatherUI(R.drawable.ic_sunny, R.color.light_beige)
            "cloudy" -> WeatherUI(R.drawable.ic_cloudy, R.color.pale_blue)
            "rainy" -> WeatherUI(R.drawable.ic_rainy, R.color.dark_shade_blue)
            else -> WeatherUI(R.drawable.ic_cloudy, R.color.pale_blue)
        }
    }
}

data class WeatherUI(
    val backgroundImageResource: Int,
    val backgroundColorResource: Int
)