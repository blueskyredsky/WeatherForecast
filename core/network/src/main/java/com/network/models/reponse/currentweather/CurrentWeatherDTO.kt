package com.network.models.reponse.currentweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class CurrentWeatherDTO(
    @SerialName("current") val currentDTO: CurrentDTO? = null,
    @SerialName("location") val locationDTO: LocationDTO? = null
)

fun CurrentWeatherDTO.toWeatherSummaryPair(): Pair<String, String> {
    val city = this.locationDTO?.name.takeIf { !it.isNullOrBlank() } ?: "Unknown Location"
    val tempInCelsius = this.currentDTO?.tempC

    val temperatureString = if (tempInCelsius != null) {
        "${tempInCelsius.roundToInt()}°"
    } else {
        "--°"
    }

    val condition = this.currentDTO?.conditionDTO?.text.takeIf {
        !it.isNullOrBlank()
    } ?: "Weather data unavailable"

    return city to "$temperatureString, $condition"
}