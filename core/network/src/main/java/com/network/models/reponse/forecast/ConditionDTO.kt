package com.network.models.reponse.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDTO(
    @SerialName("text")
    val text: String?,
    @SerialName("icon")
    val icon: String?,
    @SerialName("code")
    val code: Int?,
)
