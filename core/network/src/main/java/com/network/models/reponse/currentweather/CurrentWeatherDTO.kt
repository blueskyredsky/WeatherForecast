package com.network.models.reponse.currentweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherDTO(
    @SerialName("current") val currentDTO: CurrentDTO? = null,
    @SerialName("location") val locationDTO: LocationDTO? = null
)