package com.currentweather.data.repository

import com.currentweather.data.model.search.Search

interface SearchRepository {
    suspend fun searchLocation(cityName: String): Result<List<Search>>
}