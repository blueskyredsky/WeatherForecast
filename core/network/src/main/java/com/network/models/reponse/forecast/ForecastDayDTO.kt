package com.network.models.reponse.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDayDTO(
    @SerialName("date")
    val date: String?,
    @SerialName("date_epoch")
    val dateEpoch: Long,
    @SerialName("day")
    val day: DayDTO?,
    @SerialName("astro")
    val astro: AstroDTO?,
    @SerialName("hour")
    val hour: List<HourDTO?>?,
)
