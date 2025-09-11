import com.network.models.reponse.currentweather.CurrentDTO
import com.network.models.reponse.currentweather.LocationDTO
import com.network.models.reponse.forecast.ForecastDataDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastDTO(
    @SerialName("location")
    val locationDTO: LocationDTO? = null,
    @SerialName("current")
    val currentDTO: CurrentDTO? = null,
    @SerialName("forecast")
    val forecastDTO: ForecastDataDTO? = null,
)