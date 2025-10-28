package com.notification

interface NotificationHandler {
    fun postWeatherForecastNotification(title: String, content: String)
}