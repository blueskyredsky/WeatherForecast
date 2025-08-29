package com.currentweather.data.repository

import ForecastDTO
import com.currentweather.data.model.currentweather.CurrentWeather
import retrofit2.Response

/**
 * Repository interface for fetching forecast data.
 */
interface WeatherRepository {
    /**
     * Fetches the current weather for a given location.
     */
    suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather>

    suspend fun fetchForecast(
        location: String,
        days: Int
    ): Response<ForecastDTO>
}