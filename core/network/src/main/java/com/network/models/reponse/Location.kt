package com.network.models.reponse

import kotlinx.serialization.SerialName

data class Location(
    @SerialName("country") val country: String?,
    @SerialName("lat") val lat: Double?,
    @SerialName("localtime") val localtime: String?,
    @SerialName("localtime_epoch") val localtimeEpoch: Int?,
    @SerialName("lon") val lon: Double?,
    @SerialName("name") val name: String?,
    @SerialName("region") val region: String?,
    @SerialName("tz_id") val tzId: String?
)
