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

    override suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather> =
        withContext(ioDispatcher) {
            try {
                apiService.fetchCurrentWeather(location).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            body.toCurrentWeatherResult().fold(
                                onSuccess = { currentWeather -> Result.success(currentWeather) },
                                onFailure = { throwable -> Result.failure(RepositoryError.MappingError(throwable)) }
                            )
                        } ?: Result.failure(
                            RepositoryError.NetworkError(
                                response.code(),
                                "Response body is null"
                            )
                        )
                    } else {
                        Result.failure(
                            RepositoryError.NetworkError(
                                response.code(),
                                response.errorBody()?.string()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Result.failure(RepositoryError.UnknownError(e))
            }
        }
}