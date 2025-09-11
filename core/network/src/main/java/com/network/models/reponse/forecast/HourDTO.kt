package com.network.models.reponse.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.network.models.reponse.currentweather.ConditionDTO

@Serializable
data class HourDTO(
    @SerialName("time_epoch")
    val timeEpoch: Long? = null,
    @SerialName("time")
    val time: String? = null,
    @SerialName("temp_c")
    val tempC: Double? = null,
    @SerialName("temp_f")
    val tempF: Double? = null,
    @SerialName("is_day")
    val isDay: Int? = null,
    @SerialName("condition")
    val condition: ConditionDTO? = null,
    @SerialName("wind_mph")
    val windMph: Double? = null,
    @SerialName("wind_kph")
    val windKph: Double? = null,
    @SerialName("wind_degree")
    val windDegree: Int? = null,
    @SerialName("wind_dir")
    val windDir: String? = null,
    @SerialName("pressure_mb")
    val pressureMb: Double? = null,
    @SerialName("pressure_in")
    val pressureIn: Double? = null,
    @SerialName("precip_mm")
    val precipitationMm: Double? = null,
    @SerialName("precip_in")
    val precipitationIn: Double? = null,
    @SerialName("snow_cm")
    val snowCm: Double? = null,
    @SerialName("snow_in")
    val snowIn: Double? = null,
    @SerialName("humidity")
    val humidity: Int? = null,
    @SerialName("cloud")
    val cloud: Int? = null,
    @SerialName("feelslike_c")
    val feelsLikeC: Double? = null,
    @SerialName("feelslike_f")
    val feelsLikeF: Double? = null,
    @SerialName("windchill_c")
    val windchillC: Double? = null,
    @SerialName("windchill_f")
    val windchillF: Double? = null,
    @SerialName("heatindex_c")
    val heatIndexC: Double? = null,
    @SerialName("heatindex_f")
    val heatIndexF: Double? = null,
    @SerialName("dewpoint_c")
    val dewPointC: Double? = null,
    @SerialName("dewpoint_f")
    val dewPointF: Double? = null,
    @SerialName("will_it_rain")
    val willItRain: Int? = null,
    @SerialName("chance_of_rain")
    val chanceOfRain: Int? = null,
    @SerialName("will_it_snow")
    val willItSnow: Int? = null,
    @SerialName("chance_of_snow")
    val chanceOfSnow: Int? = null,
    @SerialName("vis_km")
    val visKm: Double? = null,
    @SerialName("vis_miles")
    val visMiles: Double? = null,
    @SerialName("gust_mph")
    val gustMph: Double? = null,
    @SerialName("gust_kph")
    val gustKph: Double? = null,
    @SerialName("uv")
    val uv: Double? = null,
)