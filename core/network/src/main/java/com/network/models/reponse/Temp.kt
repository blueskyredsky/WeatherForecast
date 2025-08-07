import com.network.models.reponse.CurrentDTO
import com.network.models.reponse.LocationDTO
import kotlinx.serialization.SerialName

data class ForecastResponseDTO(
    val location: LocationDTO,
    val current: CurrentDTO,
    val forecast: ForecastDTO,
)

data class ForecastDTO(
    val forecastDay: List<ForecastDayDTO>,
)

data class ForecastDayDTO(
    val date: String,
    @SerialName("date_epoch")
    val dateEpoch: Long,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>,
)

data class Day(
    @SerialName("maxtemp_c")
    val maxtempC: Double,
    @SerialName("maxtemp_f")
    val maxtempF: Double,
    @SerialName("mintemp_c")
    val mintempC: Double,
    @SerialName("mintemp_f")
    val mintempF: Double,
    @SerialName("avgtemp_c")
    val avgtempC: Double,
    @SerialName("avgtemp_f")
    val avgtempF: Double,
    @SerialName("maxwind_mph")
    val maxwindMph: Double,
    @SerialName("maxwind_kph")
    val maxwindKph: Double,
    @SerialName("totalprecip_mm")
    val totalprecipMm: Double,
    @SerialName("totalprecip_in")
    val totalprecipIn: Double,
    @SerialName("totalsnow_cm")
    val totalsnowCm: Double,
    @SerialName("avgvis_km")
    val avgvisKm: Double,
    @SerialName("avgvis_miles")
    val avgvisMiles: Double,
    val avghumidity: Long,
    @SerialName("daily_will_it_rain")
    val dailyWillItRain: Long,
    @SerialName("daily_chance_of_rain")
    val dailyChanceOfRain: Long,
    @SerialName("daily_will_it_snow")
    val dailyWillItSnow: Long,
    @SerialName("daily_chance_of_snow")
    val dailyChanceOfSnow: Long,
    val condition: Condition2,
    val uv: Double,
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
    @SerialName("moon_phase")
    val moonPhase: String,
    @SerialName("moon_illumination")
    val moonIllumination: Long,
    @SerialName("is_moon_up")
    val isMoonUp: Long,
    @SerialName("is_sun_up")
    val isSunUp: Long,
)

data class Hour(
    @SerialName("time_epoch")
    val timeEpoch: Long,
    val time: String,
    @SerialName("temp_c")
    val tempC: Double,
    @SerialName("temp_f")
    val tempF: Double,
    @SerialName("is_day")
    val isDay: Long,
    val condition: Condition3,
    @SerialName("wind_mph")
    val windMph: Double,
    @SerialName("wind_kph")
    val windKph: Double,
    @SerialName("wind_degree")
    val windDegree: Long,
    @SerialName("wind_dir")
    val windDir: String,
    @SerialName("pressure_mb")
    val pressureMb: Double,
    @SerialName("pressure_in")
    val pressureIn: Double,
    @SerialName("precip_mm")
    val precipMm: Double,
    @SerialName("precip_in")
    val precipIn: Double,
    @SerialName("snow_cm")
    val snowCm: Double,
    val humidity: Long,
    val cloud: Long,
    @SerialName("feelslike_c")
    val feelslikeC: Double,
    @SerialName("feelslike_f")
    val feelslikeF: Double,
    @SerialName("windchill_c")
    val windchillC: Double,
    @SerialName("windchill_f")
    val windchillF: Double,
    @SerialName("heatindex_c")
    val heatindexC: Double,
    @SerialName("heatindex_f")
    val heatindexF: Double,
    @SerialName("dewpoint_c")
    val dewpointC: Double,
    @SerialName("dewpoint_f")
    val dewpointF: Double,
    @SerialName("will_it_rain")
    val willItRain: Long,
    @SerialName("chance_of_rain")
    val chanceOfRain: Long,
    @SerialName("will_it_snow")
    val willItSnow: Long,
    @SerialName("chance_of_snow")
    val chanceOfSnow: Long,
    @SerialName("vis_km")
    val visKm: Double,
    @SerialName("vis_miles")
    val visMiles: Double,
    @SerialName("gust_mph")
    val gustMph: Double,
    @SerialName("gust_kph")
    val gustKph: Double,
    val uv: Double,
)

data class Condition3(
    val text: String,
    val icon: String,
    val code: Long,
)