package com.notification

interface NotificationHandler {
    fun isNotificationsEnabled(): Boolean
    fun postWeatherForecastNotification(location: String, weatherForecast: String)
}