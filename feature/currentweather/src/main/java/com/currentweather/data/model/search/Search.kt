package com.currentweather.data.model.search

import com.network.models.reponse.search.SearchDTO

data class Search(
    private val id: Int,
    private val name: String,
    private val region: String,
    private val country: String,
    private val lat: Double,
    private val lon: Double,
    private val url: String
)

fun List<SearchDTO>.toListSearch(): Result<List<Search>> {
    return runCatching {
        map {
            Search(
                id = it.id ?: 0,
                name = it.name.orEmpty(),
                region = it.region.orEmpty(),
                country = it.country.orEmpty(),
                lat = it.lat ?: 0.0,
                lon = it.lon ?: 0.0,
                url = it.url.orEmpty()
            )
        }
    }
}