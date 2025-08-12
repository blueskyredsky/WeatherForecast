package com.forecaset.data.model.currentweather

import com.network.models.reponse.currentweather.CurrentDTO

data class Current(
    val cloud: Int,
    val condition: Condition,
    val feelsLikeC: Double,
    val feelsLikeF: Double,
    val humidity: Int,
    val isDay: Int,
    val lastUpdated: String,
    val precipIn: Double,
    val precipMm: Double,
    val pressureIn: Double,
    val pressureMb: Double,
    val tempC: Double,
    val tempF: Double,
    val uv: Double,
    val windDegree: Int,
    val windDir: String,
    val windKph: Double,
    val windMph: Double,
    val windchillC: Double,
    val windchillF: Double
)

fun CurrentDTO.toCurrentResult(): Result<Current> {
    return runCatching {
        val requiredTempC = tempC
            ?: throw IllegalArgumentException("Temperature (C) is a mandatory field for Current and cannot be null.")
        val requiredTempF = tempF
            ?: throw IllegalArgumentException("Temperature (F) is a mandatory field for Current and cannot be null.")
        val requiredLastUpdated = lastUpdated
            ?: throw IllegalArgumentException("Last updated time is a mandatory field for Current and cannot be null.")
        val requiredConditionDTO = conditionDTO
            ?: throw IllegalArgumentException("Condition data is mandatory for Current and cannot be null.")

        Current(
            cloud = cloud ?: 0,
            condition = requiredConditionDTO.toCondition(),
            feelsLikeC = feelsLikeC ?: 0.0,
            feelsLikeF = feelsLikeF ?: 0.0,
            humidity = humidity ?: 0,
            isDay = isDay ?: 0,
            lastUpdated = requiredLastUpdated,
            precipIn = precipIn ?: 0.0,
            precipMm = precipMm ?: 0.0,
            pressureIn = pressureIn ?: 0.0,
            pressureMb = pressureMb ?: 0.0,
            tempC = requiredTempC,
            tempF = requiredTempF,
            uv = uv ?: 0.0,
            windDegree = windDegree ?: 0,
            windDir = windDir ?: "",
            windKph = windKph ?: 0.0,
            windMph = windMph ?: 0.0,
            windchillC = windchillC ?: 0.0,
            windchillF = windchillF ?: 0.0
        )
    }
}