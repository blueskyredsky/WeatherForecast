package com.forecaset.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.common.model.Result
import com.forecaset.data.model.CurrentWeather
import com.forecaset.data.repository.ForecastRepository
import com.forecaset.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val forecastRepository: ForecastRepository,
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
    }

    fun checkLocationServiceStatus() {
        locationRepository.isLocationEnabled()
            .onEach { isEnabled ->
                _locationEnabled.value = isEnabled
            }
            .launchIn(viewModelScope)
    }


    fun fetchWeatherOnLocation() {
        if (!locationRepository.hasLocationPermissions()) {
            _requestLocationPermissions.value = true
            return
        }

        if (!_locationEnabled.value) {
            _currentWeather.value = Result.Error(Exception("Location services are disabled. Please enable them."))
            return
        }

        viewModelScope.launch {
            _currentWeather.value = Result.Loading

            // Try to get location updates first, if not available, fall back to last known location.
            locationRepository.getLocationUpdates()
                .onEach { location ->
                    fetchWeather(location)
                    // For continuous weather updates based on location, remove the return statement.
                    return@onEach
                }
                .catch { e ->
                    // Handle cases where getLocationUpdates might fail (e.g., permissions revoked mid-update)
                    _currentWeather.value = Result.Error(e)
                    // Fallback to last known location if updates fail
                    locationRepository.getLastKnownLocation()
                        .onEach { lastLocation ->
                            if (lastLocation != null) {
                                fetchWeather(lastLocation)
                            } else {
                                _currentWeather.value = Result.Error(Exception("Could not get current or last known location."))
                            }
                        }
                        .catch { eLast ->
                            _currentWeather.value = Result.Error(eLast)
                        }
                        .collect{}
                }
                .collect{}
        }
    }

    /**
     * Helper function to perform the actual API call once location is available.
     */
    private fun fetchWeather(location: Location) {
        viewModelScope.launch {
            try {
                val weather = forecastRepository.fetchCurrentWeather(
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
        _requestLocationPermissions.value = false // Reset request flag
        fetchWeatherOnLocation()
    }

    fun onLocationPermissionsDenied() {
        _locationPermissionGranted.value = false
        _requestLocationPermissions.value = false // Reset request flag
        _currentWeather.value = Result.Error(Exception("Location permissions denied. Cannot fetch weather."))
    }

    fun permissionRequestHandled() {
        _requestLocationPermissions.value = false
    }
}