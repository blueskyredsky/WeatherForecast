package com.currentweather.data.model.forecast

import ForecastDTO
import com.common.model.currentweather.Condition
import com.common.model.currentweather.Current
import com.common.model.currentweather.Location
import com.currentweather.data.model.common.toCondition
import com.currentweather.data.model.common.toCurrentResult
import com.currentweather.data.model.common.toLocationResult
import com.network.models.reponse.forecast.HourDTO

data class Forecast(
    val location: Location,
    val current: Current,
    val forecast: ForecastData
)

fun ForecastDTO.toForecastResult(): Result<Forecast> {
    return runCatching {
        val currentDtoNonNull = currentDTO
            ?: throw IllegalArgumentException("CurrentDTO is missing for Forecast and cannot be null.")
        val locationDtoNonNull = locationDTO
            ?: throw IllegalArgumentException("LocationDTO is missing for Forecast and cannot be null.")
        val forecastDtoNonNull = forecastDTO
            ?: throw IllegalArgumentException("ForecastDTO is missing for Forecast and cannot be null.")

        val current = currentDtoNonNull.toCurrentResult().getOrThrow()
        val location = locationDtoNonNull.toLocationResult().getOrThrow()
        val forecast = forecastDtoNonNull.toForecastResult().getOrThrow()

        Forecast(
            current = current,
            location = location,
            forecast = forecast
        )
    }
}

data class ForecastData(
    val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val dateEpoch: Long,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>,
)

data class Day(
    val maxTempC: Double,
    val maxTempF: Double,
    val minTempC: Double,
    val minTempF: Double,
    val avgTempC: Double,
    val avgTempF: Double,
    val maxWindMph: Double,
    val maxWindKph: Double,
    val totalPrecipitationIn: Double,
    val totalSnowCm: Double,
    val avgVisKm: Double,
    val avgVisMiles: Double,
    val avgHumidity: Int,
    val dailyWillItRain: Int,
    val dailyChanceOfRain: Int,
    val dailyWillItSnow: Int,
    val dailyChanceOfSnow: Int,
    val condition: Condition,
    val uv: Double
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moonPhase: String,
    val moonIllumination: Int,
    val isMoonUp: Int,
    val isSunUp: Int,
)

data class Hour(
    val timeEpoch: Long,
    val time: String,
    val tempC: Double,
    val tempF: Double,
    val isDay: Int,
    val condition: Condition,
    val windMph: Double,
    val windKph: Double,
    val windDegree: Int,
    val windDir: String,
    val pressureMb: Double,
    val pressureIn: Double,
    val precipitationMm: Double,
    val precipitationIn: Double,
    val snowCm: Double,
    val snowIn: Double,
    val humidity: Int,
    val cloud: Int,
    val feelsLikeC: Double,
    val feelsLikeF: Double,
    val windchillC: Double,
    val windchillF: Double,
    val heatIndexC: Double,
    val heatIndexF: Double,
    val dewPointC: Double,
    val dewPointF: Double,
    val willItRain: Int,
    val chanceOfRain: Int,
    val willItSnow: Int,
    val chanceOfSnow: Int,
    val visKm: Double,
    val visMiles: Double,
    val gustMph: Double,
    val gustKph: Double,
    val uv: Double,
)

fun HourDTO.toHourResult(): Result<Hour> {
    return runCatching {
        val requiredCondition = condition
            ?: throw IllegalArgumentException("Condition is a mandatory field for Hour and cannot be null.")

        Hour(
            timeEpoch = timeEpoch ?: 0,
            time = time ?: "",
            tempC = tempC ?: 0.0,
            tempF = tempF ?: 0.0,
            isDay = isDay ?: 0,
            condition = requiredCondition.toCondition(),
            windMph = windMph ?: 0.0,
            windKph = windKph ?: 0.0,
            windDegree = windDegree ?: 0,
            windDir = windDir ?: "",
            pressureMb = pressureMb ?: 0.0,
            pressureIn = pressureIn ?: 0.0,
            precipitationMm = precipitationMm ?: 0.0,
            precipitationIn = precipitationIn ?: 0.0,
            snowCm = snowCm ?: 0.0,
            snowIn = snowIn ?: 0.0,
            humidity = humidity ?: 0,
            cloud = cloud ?: 0,
            feelsLikeC = feelsLikeC ?: 0.0,
            feelsLikeF = feelsLikeF ?: 0.0,
            windchillC = windchillC ?: 0.0,
            windchillF = windchillF ?: 0.0,
            heatIndexC = heatIndexC ?: 0.0,
            heatIndexF = heatIndexF ?: 0.0,
            dewPointC = dewPointC ?: 0.0,
            dewPointF = dewPointF ?: 0.0,
            willItRain = willItRain ?: 0,
            chanceOfRain = chanceOfRain ?: 0,
            willItSnow = willItSnow ?: 0,
            chanceOfSnow = chanceOfSnow ?: 0,
            visKm = visKm ?: 0.0,
            visMiles = visMiles ?: 0.0,
            gustMph = gustMph ?: 0.0,
            gustKph = gustKph ?: 0.0,
            uv = uv ?: 0.0,
        )
    }

}