package com.forecaset.data.repository

import com.forecaset.data.model.CurrentWeather
import com.forecaset.data.model.error.RepositoryError
import com.forecaset.data.model.toCurrentWeatherResult
import com.network.ApiService
import com.reza.threading.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultForecastRepository @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ForecastRepository {
    override suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather> = withContext(ioDispatcher) {
        try {
            val response = apiService.fetchCurrentWeather(location)
            if (response.isSuccessful) {
                // If body is null, or mapping fails, toCurrentWeatherResult() will return Result.failure
                // We map that failure to a more specific repository error type
                response.body()?.toCurrentWeatherResult()?.getOrElse { exception ->
                    return Result.failure(RepositoryError.MappingError(exception))
                } ?: Result.failure(RepositoryError.NetworkError(response.code(), "Response body is null"))
            } else {
                Result.failure(RepositoryError.NetworkError(response.code(), response.errorBody()?.string()))
            }
        } catch (e: Exception) { // Catch any other unexpected exceptions (e.g., network timeout)
            Result.failure(RepositoryError.UnknownError(e))
        }
    }
}