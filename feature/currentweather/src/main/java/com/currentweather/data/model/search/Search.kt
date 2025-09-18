package com.currentweather.data.model.search

import com.network.models.reponse.search.SearchDTO

data class Search(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
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