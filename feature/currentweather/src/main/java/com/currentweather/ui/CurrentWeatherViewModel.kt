package com.currentweather.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.common.R
import com.common.model.ErrorType
import com.common.model.error.RepositoryError
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.model.forecast.Forecast
import com.currentweather.data.repository.LocationRepository
import com.currentweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getOrThrow

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var hasFetchedCurrentWeather = false

    private val _weatherUIState = MutableStateFlow<WeatherUIState>(WeatherUIState.Idle)
    val weatherUIState = _weatherUIState.asStateFlow()

    private val _weatherUIData = MutableStateFlow(getWeatherUI(""))
    val weatherUIData = _weatherUIData.asStateFlow()

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
            _weatherUIState.value =
                WeatherUIState.Error(errorType = ErrorType.LocationPermissionDenied)
        }
    }

    fun startObservingLocationAndWeather() {
        locationRepository.isLocationEnabled()
            .onEach { isEnabled ->
                if (!isEnabled) {
                    _weatherUIState.value =
                        WeatherUIState.Error(errorType = ErrorType.LocationServicesDisabled)
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
        if (_weatherUIState.value is WeatherUIState.Loading) return

        _weatherUIState.value = WeatherUIState.Loading

        viewModelScope.launch {
            try {
                val location = locationRepository.getLocationUpdates().first()
                val locationString = "${location.latitude},${location.longitude}"

                val (currentWeather, forecast) = coroutineScope {
                    val currentWeatherDeferred =
                        async { weatherRepository.fetchCurrentWeather(locationString) }
                    val forecastDeferred =
                        async { weatherRepository.fetchForecast(locationString, 1) }

                    currentWeatherDeferred.await().getOrThrow() to forecastDeferred.await().getOrThrow()
                }

                _weatherUIState.value = WeatherUIState.Success(
                    currentWeather = currentWeather,
                    forecast = forecast
                )

                _weatherUIData.value = getWeatherUI(currentWeather.current.condition.text)
            } catch (e: Exception) {
                val errorType = when (e) {
                    is RepositoryError.NetworkError -> ErrorType.NetworkError
                    is RepositoryError.NoDataError -> ErrorType.NoDataError
                    is RepositoryError.MappingError -> ErrorType.MappingError
                    else -> ErrorType.UnknownError
                }
                _weatherUIState.value = WeatherUIState.Error(errorType = errorType)
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
        _weatherUIState.value = WeatherUIState.Idle
        hasFetchedCurrentWeather = false
        fetchWeatherOnLocation()
    }

    private fun getWeatherUI(text: String): WeatherUI {
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

/**
 * Represents the UI elements for displaying weather information.
 *
 * @property backgroundImageResource The resource ID for the background image.
 * @property backgroundColorResource The resource ID for the background color.
 * @property textColorResource The resource ID for the text color.
 */
data class WeatherUI(
    @DrawableRes val backgroundImageResource: Int,
    @ColorRes val backgroundColorResource: Int,
    @ColorRes val textColorResource: Int = R.color.black
)

/**
 * Represents the different states of the weather UI.
 */
sealed interface WeatherUIState {
    /**
     * Represents the idle state of the UI.
     */
    data object Idle : WeatherUIState
    /**
     * Represents the loading state of the UI.
     */
    data object Loading : WeatherUIState
    /**
     * Represents the success state of the UI, containing current weather and forecast data.
     * @param currentWeather The current weather information.
     * @param forecast The forecast information.
     */
    data class Success(
        val currentWeather: CurrentWeather?,
        val forecast: Forecast?
    ) : WeatherUIState

    /**
     * Represents the error state of the UI.
     * @param errorType The type of error that occurred.
     */
    data class Error(val errorType: ErrorType) : WeatherUIState
}