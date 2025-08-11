import com.network.models.reponse.CurrentDTO
import com.network.models.reponse.LocationDTO
import com.network.models.reponse.forecast.ForecastDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDTO(
    @SerialName("location")
    val location: LocationDTO?,
    @SerialName("current")
    val current: CurrentDTO?,
    @SerialName("forecast")
    val forecast: ForecastDTO?,
)