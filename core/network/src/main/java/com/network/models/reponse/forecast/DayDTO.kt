package com.network.models.reponse.forecast

import com.network.models.reponse.currentweather.ConditionDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DayDTO(
    @SerialName("maxtemp_c")
    val maxTempC: Double? = null,
    @SerialName("maxtemp_f")
    val maxTempF: Double? = null,
    @SerialName("mintemp_c")
    val minTempC: Double? = null,
    @SerialName("mintemp_f")
    val minTempF: Double? = null,
    @SerialName("avgtemp_c")
    val avgTempC: Double? = null,
    @SerialName("avgtemp_f")
    val avgTempF: Double? = null,
    @SerialName("maxwind_mph")
    val maxWindMph: Double? = null,
    @SerialName("maxwind_kph")
    val maxWindKph: Double? = null,
    @SerialName("totalprecip_mm")
    val totalPrecipitationMm: Double? = null,
    @SerialName("totalprecip_in")
    val totalPrecipitationIn: Double? = null,
    @SerialName("totalsnow_cm")
    val totalSnowCm: Double? = null,
    @SerialName("avgvis_km")
    val avgVisKm: Double? = null,
    @SerialName("avgvis_miles")
    val avgVisMiles: Double? = null,
    @SerialName("avghumidity")
    val avgHumidity: Int? = null,
    @SerialName("daily_will_it_rain")
    val dailyWillItRain: Int? = null,
    @SerialName("daily_chance_of_rain")
    val dailyChanceOfRain: Int? = null,
    @SerialName("daily_will_it_snow")
    val dailyWillItSnow: Int? = null,
    @SerialName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int? = null,
    @SerialName("condition")
    val condition: ConditionDTO? = null,
    @SerialName("uv")
    val uv: Double? = null
)