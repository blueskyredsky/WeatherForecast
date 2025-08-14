package com.currentweather.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.common.model.ErrorType
import com.common.model.Result
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.repository.CurrentWeatherRepository
import com.currentweather.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val currentWeatherRepository: CurrentWeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

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
        if (_currentWeather.value is Result.Loading) return

        _currentWeather.value = Result.Loading

        viewModelScope.launch {
            try {
                locationRepository.getLocationUpdates()
                    .onEach { location ->
                        fetchWeather(location)
                        return@onEach // Only take the first successful location update for initial fetch
                    }
                    .catch { e ->
                        _currentWeather.value = Result.Error(e, ErrorType.UNKNOWN)
                        // Fallback to last known location if updates fail
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
                    .collect()
            } catch (e: Exception) {
                _currentWeather.value = Result.Error(e, ErrorType.UNKNOWN)
            }
        }
    }

    /**
     * Helper function to perform the actual API call once location is available.
     */
    private fun fetchWeather(location: Location) {
        viewModelScope.launch {
            try {
                val weather = currentWeatherRepository.fetchCurrentWeather(
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
}