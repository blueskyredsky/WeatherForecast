package com.forecaset.data.model

import com.network.models.reponse.LocationDTO

data class Location(
    val country: String,
    val lat: Double,
    val localtime: String,
    val lon: Double,
    val name: String,
    val region: String
)

fun LocationDTO.toLocationResult(): Result<Location> {
    return runCatching {
         Location(
            country = country ?: throw IllegalStateException("Country is a mandatory field and cannot be null"),
            lat = lat ?: throw IllegalStateException("Latitude is a mandatory field and cannot be null"),
            localtime = localtime ?: "",
            lon = lon ?: throw IllegalStateException("Longitude is a mandatory field and cannot be null"),
            name = name ?: "",
            region = region ?: "",
        )
    }
}