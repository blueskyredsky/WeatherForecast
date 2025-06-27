package com.network.models.reponse


data class Root(
    val location: Location,
    val current: Current,
    val forecast: Forecast,
    val alerts: Alerts,
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tzId: String,
    val localtimeEpoch: Long,
    val localtime: String,
)

data class Current(
    val lastUpdatedEpoch: Long,
    val lastUpdated: String,
    val tempC: Double,
    val tempF: Double,
    val isDay: Long,
    val condition: Condition,
    val windMph: Double,
    val windKph: Double,
    val windDegree: Long,
    val windDir: String,
    val pressureMb: Long,
    val pressureIn: Double,
    val precipMm: Long,
    val precipIn: Long,
    val humidity: Long,
    val cloud: Long,
    val feelslikeC: Long,
    val feelslikeF: Double,
    val visKm: Long,
    val visMiles: Long,
    val uv: Long,
    val gustMph: Double,
    val gustKph: Double,
    val airQuality: AirQuality,
)

data class Condition(
    val text: String,
    val icon: String,
    val code: Long,
)

data class AirQuality(
    val co: Double,
    val no2: Double,
    val o3: Double,
    val so2: Long,
    val pm25: Double,
    val pm10: Long,
    val usEpaIndex: Long,
    val gbDefraIndex: Long,
)

data class Forecast(
    val forecastday: List<Forecastday>,
)

data class Forecastday(
    val date: String,
    val dateEpoch: Long,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>,
)

data class Day(
    val maxtempC: Double,
    val maxtempF: Double,
    val mintempC: Double,
    val mintempF: Double,
    val avgtempC: Double,
    val avgtempF: Double,
    val maxwindMph: Double,
    val maxwindKph: Double,
    val totalprecipMm: Long,
    val totalprecipIn: Long,
    val avgvisKm: Long,
    val avgvisMiles: Long,
    val avghumidity: Long,
    val dailyWillItRain: Long,
    val dailyChanceOfRain: Long,
    val dailyWillItSnow: Long,
    val dailyChanceOfSnow: Long,
    val condition: Condition2,
    val uv: Long,
)

data class Condition2(
    val text: String,
    val icon: String,
    val code: Long,
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moonPhase: String,
    val moonIllumination: String,
)

data class Hour(
    val timeEpoch: Long,
    val time: String,
    val tempC: Double,
    val tempF: Double,
    val isDay: Long,
    val condition: Condition3,
    val windMph: Double,
    val windKph: Double,
    val windDegree: Long,
    val windDir: String,
    val pressureMb: Long,
    val pressureIn: Double,
    val precipMm: Long,
    val precipIn: Long,
    val humidity: Long,
    val cloud: Long,
    val feelslikeC: Double,
    val feelslikeF: Double,
    val windchillC: Double,
    val windchillF: Double,
    val heatindexC: Double,
    val heatindexF: Double,
    val dewpointC: Double,
    val dewpointF: Double,
    val willItRain: Long,
    val chanceOfRain: Long,
    val willItSnow: Long,
    val chanceOfSnow: Long,
    val visKm: Long,
    val visMiles: Long,
    val gustMph: Long,
    val gustKph: Double,
    val uv: Long,
)

data class Condition3(
    val text: String,
    val icon: String,
    val code: Long,
)

data class Alerts(
    val alert: List<Alert>,
)

data class Alert(
    val headline: String,
    val msgtype: Any?,
    val severity: Any?,
    val urgency: Any?,
    val areas: Any?,
    val category: String,
    val certainty: Any?,
    val event: String,
    val note: Any?,
    val effective: String,
    val expires: String,
    val desc: String,
    val instruction: String,
)
