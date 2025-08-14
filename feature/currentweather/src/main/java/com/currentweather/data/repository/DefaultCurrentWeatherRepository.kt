package com.currentweather.data.repository

import com.common.model.error.RepositoryError
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.model.currentweather.toCurrentWeatherResult
import com.network.ApiService
import com.reza.threading.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultCurrentWeatherRepository @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CurrentWeatherRepository {

    override suspend fun fetchCurrentWeather(location: String): Result<CurrentWeather> =
        withContext(ioDispatcher) {
            try {
                apiService.fetchCurrentWeather(location).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            val conversionResult: Result<CurrentWeather> = body.toCurrentWeatherResult().fold(
                                onSuccess = { currentWeather -> Result.success(currentWeather) },
                                onFailure = { throwable -> Result.failure(RepositoryError.MappingError(throwable)) }
                            )
                            conversionResult
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