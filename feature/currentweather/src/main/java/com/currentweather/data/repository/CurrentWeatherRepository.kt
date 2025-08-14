package com.currentweather.data.repository

import com.currentweather.data.model.currentweather.CurrentWeather

/**
 * Repository interface for fetching forecast data.
 */
interface CurrentWeatherRepository {
    /**
     * Fetches the current weather for a given location.
     */
    suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather>
}