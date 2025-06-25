package com.forecaset.navigation


sealed class ForecastRoutes(val route: String) {
    data object ForecastHome : ForecastRoutes("forecast_home")
}

sealed interface ForecastNavCallback {
    data class NavigateToDetail(val locationName: String) : ForecastNavCallback
}