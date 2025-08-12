package com.common.model.currentweather

data class Location(
    val country: String,
    val lat: Double,
    val localtime: String,
    val long: Double,
    val name: String,
    val region: String
)