package com.forecaset.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.common.model.ErrorType
import com.common.model.Result
import com.forecaset.data.model.CurrentWeather
import com.forecaset.data.repository.ForecastRepository
import com.forecaset.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
            _currentWeather.value = Result.Error(Exception("Location permissions denied."), ErrorType.LOCATION_PERMISSIONS_DENIED)
            return
        }

        if (!_locationEnabled.value) {
            _currentWeather.value = Result.Error(Exception("Location services are disabled."), ErrorType.LOCATION_SERVICES_DISABLED)
            return
        }

        viewModelScope.launch {
            _currentWeather.value = Result.Loading

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
        _requestLocationPermissions.value = false
        fetchWeatherOnLocation()
    }

    fun permissionRequestHandled() {
        _requestLocationPermissions.value = false
    }
}