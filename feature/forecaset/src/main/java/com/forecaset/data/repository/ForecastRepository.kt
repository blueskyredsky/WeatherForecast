package com.forecaset.data.repository

import com.forecaset.data.model.CurrentWeather

/**
 * Repository interface for fetching forecast data.
 */
interface ForecastRepository {
    /**
     * Fetches the current weather for a given location.
     */
    suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather>
}