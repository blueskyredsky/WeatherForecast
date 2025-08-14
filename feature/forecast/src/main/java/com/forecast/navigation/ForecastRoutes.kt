package com.forecast.navigation

sealed class ForecastRoutes(val route: String) {
    data object ItemForecast : ForecastRoutes("item_forecast_screen/{locationName}") {
        fun createRoute(locationName: String) = "item_forecast_screen/$locationName"
    }
}

sealed interface ForecastNavCallback {
    data object NavigateBackToForecastHome : ForecastNavCallback
}