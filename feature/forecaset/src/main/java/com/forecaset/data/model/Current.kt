package com.forecaset.data.model

data class Current(
    val cloud: Int,
    val condition: Condition,
    val dewPointC: Double,
    val dewPointF: Double,
    val feelsLikeC: Double,
    val feelsLikeF: Double,
    val gustKph: Double,
    val gustMph: Double,
    val heatIndexC: Double,
    val heatIndexF: Double,
    val humidity: Int,
    val isDay: Int,
    val lastUpdated: String,
    val lastUpdatedEpoch: Int,
    val precipIn: Double,
    val precipMm: Double,
    val pressureIn: Double,
    val pressureMb: Double,
    val tempC: Double,
    val tempF: Double,
    val uv: Double,
    val visKm: Double,
    val visMiles: Double,
    val windDegree: Int,
    val windDir: String,
    val windKph: Double,
    val windMph: Double,
    val windchillC: Double,
    val windchillF: Double
)