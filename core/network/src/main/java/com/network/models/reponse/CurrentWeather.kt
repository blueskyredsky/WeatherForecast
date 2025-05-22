package com.network.models.reponse

import kotlinx.serialization.SerialName

data class CurrentWeather(
    @SerialName("current") val current: Current?,
    @SerialName("location") val location: Location?
)