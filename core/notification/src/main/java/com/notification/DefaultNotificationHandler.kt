package com.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_NOTIFICATIONS = 5
private const val TARGET_ACTIVITY_NAME = "com.weatherforcast.MainActivity"
private const val WEATHER_FORECAST_NOTIFICATION_CHANNEL_ID = ""

@Singleton
class DefaultNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationHandler {

    private val CHANNEL_ID = "weather_updates_channel"
    private val NOTIFICATION_ID = 101

    override fun showWeatherNotification(title: String, content: String) {
        createNotificationChannel()

        // Intent to launch MainActivity, which will handle navigation to the weather screen
        val intent = Intent(context, Class.forName(TARGET_ACTIVITY_NAME)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            pendingIntentFlags
        )

//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_ic_menu_today)
//            .setContentTitle(title)
//            .setContentText(content)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(context)) { // Use injected context
//            // Check for notification permission if targeting Android 13+ (API 33+)
//            notify(NOTIFICATION_ID, builder.build())
//        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Updates"
            val descriptionText = "Current weather conditions for your location."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        WEATHER_FORECAST_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notification_weather_forecast_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notification_weather_forecast_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}