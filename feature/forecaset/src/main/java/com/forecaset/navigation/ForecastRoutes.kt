package com.forecaset.navigation


sealed class ForecastRoutes(val route: String) {
    data object ForecastHome : ForecastRoutes("forecast_home")
    // Add other screens specific to the forecast module if any
}

// Define actions that the Forecast feature needs the App module to handle.
// This is the bridge for cross-feature navigation.
sealed interface ForecastNavCallback {
    data class NavigateToDetail(val locationId: String) : ForecastNavCallback
    // Add other actions that involve navigating outside the forecast module
}