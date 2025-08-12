package com.network.models.reponse.currentweather

import com.network.models.reponse.currentweather.LocationDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherDTO(
    @SerialName("current") val currentDTO: CurrentDTO?,
    @SerialName("location") val locationDTO: LocationDTO?
)