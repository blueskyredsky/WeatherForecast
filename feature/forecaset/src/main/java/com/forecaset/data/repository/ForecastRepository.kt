package com.forecaset.data.repository

import com.forecaset.data.model.CurrentWeather

interface ForecastRepository {
    suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather>
}