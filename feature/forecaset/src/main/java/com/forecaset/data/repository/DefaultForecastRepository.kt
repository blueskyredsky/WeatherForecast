package com.forecaset.data.repository

import com.forecaset.data.model.CurrentWeather
import com.network.ApiService
import javax.inject.Inject

class DefaultForecastRepository @Inject constructor(private val apiService: ApiService) : ForecastRepository {
    override suspend fun fetchCurrentWeather(location: String): CurrentWeather {
        TODO("Not yet implemented")
    }

}