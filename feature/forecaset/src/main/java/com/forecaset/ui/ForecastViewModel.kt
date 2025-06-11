package com.forecaset.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forecaset.data.model.CurrentWeather
import com.forecaset.data.repository.ForecastRepository
import com.forecaset.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val forecastRepository: ForecastRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _currentWeather = MutableStateFlow<Result<CurrentWeather>?>(null)
    val currentWeather: StateFlow<Result<CurrentWeather>?> = _currentWeather.asStateFlow()

    fun getLocationUpdates() = locationRepository.getLocationUpdates()

    fun getLastKnownLocation() = locationRepository.getLastKnownLocation()

    fun isLocationEnabled() = locationRepository.isLocationEnabled()

    private fun hasLocationPermissions() = locationRepository.hasLocationPermissions()

    fun loadCurrentWeather() {
        if (hasLocationPermissions()) {
            viewModelScope.launch {
                locationRepository.getLastKnownLocation().collectLatest { lastLocation ->
                    forecastRepository.fetchCurrentWeather(lastLocation?.latitude.toString() + "," + lastLocation?.longitude.toString())
                }
            }
        } else {

        }
    }
}