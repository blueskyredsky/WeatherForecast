package com.notification

interface NotificationHandler {
    fun areNotificationsEnabled(): Boolean
    fun postWeatherForecastNotification(location: String, weatherForecast: String)
}