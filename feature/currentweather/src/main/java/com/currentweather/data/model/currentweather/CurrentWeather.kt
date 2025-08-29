package com.currentweather.data.model.currentweather

import com.common.model.currentweather.Current
import com.common.model.currentweather.Location
import com.currentweather.data.model.common.toCurrentResult
import com.currentweather.data.model.common.toLocationResult
import com.network.models.reponse.currentweather.CurrentWeatherDTO

data class CurrentWeather(
    val current: Current,
    val location: Location
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