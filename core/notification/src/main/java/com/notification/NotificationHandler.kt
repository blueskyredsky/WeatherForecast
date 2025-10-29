package com.notification

interface NotificationHandler {
    fun postWeatherForecastNotification(location: String, weatherForecast: String)
}