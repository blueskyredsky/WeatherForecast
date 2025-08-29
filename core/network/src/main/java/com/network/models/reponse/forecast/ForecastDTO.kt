import com.network.models.reponse.currentweather.CurrentDTO
import com.network.models.reponse.currentweather.LocationDTO
import com.network.models.reponse.forecast.ForecastDataDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDTO(
    @SerialName("location")
    val locationDTO: LocationDTO?,
    @SerialName("current")
    val currentDTO: CurrentDTO?,
    @SerialName("forecast")
    val forecastDTO: ForecastDataDTO?,
)