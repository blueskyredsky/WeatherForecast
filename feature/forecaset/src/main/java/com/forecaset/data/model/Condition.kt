package com.forecaset.data.model

import com.network.models.reponse.ConditionDTO

data class Condition(
    val code: Int,
    val icon: String,
    val text: String
)

fun ConditionDTO.toCondition(): Condition {
    return Condition(
        code = code ?: 0,
        icon = icon ?: "",
        text = text ?: ""
    )
}