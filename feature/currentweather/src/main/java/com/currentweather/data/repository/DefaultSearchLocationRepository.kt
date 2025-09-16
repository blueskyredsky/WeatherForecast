package com.currentweather.data.repository

import com.common.model.error.RepositoryError
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.model.currentweather.toCurrentWeatherResult
import com.currentweather.data.model.search.Search
import com.currentweather.data.model.search.toListSearch
import com.network.ApiService
import com.reza.threading.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultSearchLocationRepository @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SearchRepository {

    override suspend fun searchLocation(cityName: String): Result<List<Search>> =
        withContext(ioDispatcher) {
            try {
                apiService.searchLocation(cityName).let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { body ->
                            body.toListSearch().fold(
                                onSuccess = { searchedLocations -> Result.success(searchedLocations) },
                                onFailure = { throwable ->
                                    Result.failure(
                                        RepositoryError.MappingError(
                                            throwable
                                        )
                                    )
                                }
                            )
                        } ?: Result.failure(RepositoryError.NoDataError("Response body is null"))
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