package com.forecaset.data.model

import com.network.models.reponse.CurrentWeatherDTO

data class CurrentWeather(
    private val current: Current?,
    private val location: Location?
)

fun CurrentWeatherDTO.toCurrentWeatherResult(): Result<CurrentWeather> {
    return runCatching {
        val currentDtoNonNull = currentDTO
            ?: throw IllegalArgumentException("CurrentDTO is missing for CurrentWeather and cannot be null.")
        val locationDtoNonNull = locationDTO
            ?: throw IllegalArgumentException("LocationDTO is missing for CurrentWeather and cannot be null.")

        val current = currentDtoNonNull.toCurrentResult().getOrThrow()
        val location = locationDtoNonNull.toLocationResult().getOrThrow()

        CurrentWeather(
            current = current,
            location = location
        )
    }
}