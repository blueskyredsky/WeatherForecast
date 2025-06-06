package com.network.models.reponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDTO(
    @SerialName("code") val code: Int?,
    @SerialName("icon") val icon: String?,
    @SerialName("text") val text: String?
)