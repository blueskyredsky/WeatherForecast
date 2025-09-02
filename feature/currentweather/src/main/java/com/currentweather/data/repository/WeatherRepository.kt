package com.currentweather.data.repository

import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.model.forecast.Forecast

/**
 * Repository interface for fetching forecast data.
 */
interface WeatherRepository {
    /**
     * Fetches the current weather for a given location.
     */
    suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather>

    /**
     * Fetches the forecast for a given location and number of days.
     */
    suspend fun fetchForecast(location: String, days: Int): Result<Forecast>
}