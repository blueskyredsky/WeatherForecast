package com.currentweather.navigation

sealed class CurrentWeatherRoutes(val route: String) {
    data object CurrentWeatherHome : CurrentWeatherRoutes("current_weather_home")
}

sealed interface CurrentWeatherNavCallback {
    data class NavigateToDetail(val locationName: String) : CurrentWeatherNavCallback
}