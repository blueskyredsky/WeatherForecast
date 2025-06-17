package com.forecaset.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forecaset.data.model.CurrentWeather
import com.forecaset.data.repository.ForecastRepository
import com.forecaset.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val currentWeather: StateFlow<Result<CurrentWeather?>?> = _currentWeather.asStateFlow()

    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()

    private val _locationEnabled = MutableStateFlow(false)
    val locationEnabled: StateFlow<Boolean> = _locationEnabled.asStateFlow()

    // State to communicate a need to request permissions
    private val _requestLocationPermissions = MutableStateFlow(false)
    val requestLocationPermissions: StateFlow<Boolean> = _requestLocationPermissions.asStateFlow()

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

    /**
     * Call this when the view is ready to fetch weather.
     * It will handle permission checks and location acquisition.
     */
    fun fetchWeatherOnLocation() {
        // Check permissions via the repository
        if (!locationRepository.hasLocationPermissions()) {
            _requestLocationPermissions.value = true
            return
        }

        if (!_locationEnabled.value) {
            _currentWeather.value = Result.Error(Exception("Location services are disabled. Please enable them."))
            return
        }

        viewModelScope.launch {
            _currentWeather.value = Result.Loading // Show loading state

            // Try to get location updates first, if not available, fall back to last known location.
            locationRepository.getLocationUpdates()
                .onEach { location ->
                    fetchWeather(location)
                    // If you only need one location for the weather API, you might want to stop further updates
                    // by cancelling the flow collection or using .first() if appropriate.
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
                        .collect{} // Collect to trigger the flow
                }
                .collect{} // Collect to trigger the flow
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

    /**
     * Call this from your Activity/Fragment once permissions are granted.
     */
    fun onLocationPermissionsGranted() {
        _locationPermissionGranted.value = true
        _requestLocationPermissions.value = false // Reset request flag
        fetchWeatherOnLocation() // Retry fetching weather now that permissions are granted
    }

    /**
     * Call this if permissions are denied.
     */
    fun onLocationPermissionsDenied() {
        _locationPermissionGranted.value = false
        _requestLocationPermissions.value = false // Reset request flag
        _currentWeather.value = Result.Error(Exception("Location permissions denied. Cannot fetch weather."))
    }

    /**
     * Acknowledge that the UI has handled the permission request.
     */
    fun permissionRequestHandled() {
        _requestLocationPermissions.value = false
    }

    // You can keep these for direct observation if needed, but fetchWeatherOnLocation orchestrates them.
    fun getLocationUpdates() = locationRepository.getLocationUpdates()
    fun getLastKnownLocation() = locationRepository.getLastKnownLocation()
    fun isLocationEnabled() = locationRepository.isLocationEnabled()
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}