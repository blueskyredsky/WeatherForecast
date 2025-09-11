package com.network.models.reponse.currentweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDTO(
    @SerialName("code") val code: Int? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("text") val text: String? = null
)