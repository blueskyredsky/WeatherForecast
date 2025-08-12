import com.network.models.reponse.currentweather.CurrentDTO
import com.network.models.reponse.currentweather.LocationDTO
import com.network.models.reponse.forecast.ForecastDataDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDTO(
    @SerialName("location")
    val location: LocationDTO?,
    @SerialName("current")
    val current: CurrentDTO?,
    @SerialName("forecast")
    val forecast: ForecastDataDTO?,
)