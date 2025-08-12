package com.forecaset.data.repository

import ForecastDTO
import com.forecaset.data.model.currentweather.CurrentWeather

/**
 * Repository interface for fetching forecast data.
 */
interface ForecastRepository {
    /**
     * Fetches the current weather for a given location.
     */
    suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather>

    /**
     * Fetches the forecast for a given location.
     */
    /*suspend fun fetchForecast(location: String): Result<ForecastDTO>*/
}