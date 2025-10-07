package com.notification

interface NotificationHandler {

    /**
     * Shows a weather update notification with a title, content, and a tap action
     * that navigates to the current weather screen.
     *
     * @param title The title of the notification.
     * @param content The main body text of the notification.
     */
    fun showWeatherNotification(title: String, content: String)
}