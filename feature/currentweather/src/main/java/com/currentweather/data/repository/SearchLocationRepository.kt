package com.currentweather.data.repository

import com.currentweather.data.model.search.Search

/**
 * Repository to handle location search
 */
interface SearchLocationRepository {
    /**
     * Search for locations based on the provided city name.
     *
     * @param cityName The name of the city to search for.
     * @return A [Result] containing a list of [Search] objects if the search is successful,
     */
    suspend fun searchLocation(cityName: String): Result<List<Search>>
}