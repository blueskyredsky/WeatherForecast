package com.network.models.reponse.currentweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO(
    @SerialName("country") val country: String? = null,
    @SerialName("lat") val lat: Double? = null,
    @SerialName("localtime") val localtime: String? = null,
    @SerialName("localtime_epoch") val localtimeEpoch: Int? = null,
    @SerialName("lon") val lon: Double? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("region") val region: String? = null,
    @SerialName("tz_id") val tzId: String? = null
)