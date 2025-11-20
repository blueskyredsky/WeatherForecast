package com.notification

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TARGET_ACTIVITY_NAME = "com.weatherforcast.MainActivity"
private const val WEATHER_FORECAST_NOTIFICATION_REQUEST_CODE = 0
private const val WEATHER_FORECAST_NOTIFICATION_ID = 1
private const val WEATHER_FORECAST_NOTIFICATION_CHANNEL_ID = ""
private const val WEATHER_FORECAST_NOTIFICATION_GROUP = "NEWS_NOTIFICATIONS"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.weatherforecast.sampleapp.com"
private const val DEEP_LINK_FORECAST_PATH = "forecast"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_FORECAST_PATH"
const val DEEP_LINK_LOCATION_KEY = "locationName"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_LOCATION_KEY}"

@Singleton
class DefaultNotificationHandler @Inject constructor(
    @param:ApplicationContext private val context: Context
) : NotificationHandler {

    override fun postWeatherForecastNotification(location: String, weatherForecast: String) =
        with(context) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission.POST_NOTIFICATIONS
                ) != PERMISSION_GRANTED
            ) {
                return
            }

            val weatherForecastNotification = createWeatherForecastNotification {
                setSmallIcon(com.common.R.drawable.ic_sunny_small)
                    .setContentTitle(location)
                    .setContentText(weatherForecast)
                    .setContentIntent(weatherForecastPendingIntent(location))
                    .setGroup(WEATHER_FORECAST_NOTIFICATION_GROUP)
                    .setAutoCancel(true)
            }

            // Send the notifications
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(
                WEATHER_FORECAST_NOTIFICATION_ID,
                weatherForecastNotification
            )
        }

    override fun areNotificationsEnabled(): Boolean {
        // For Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context,
                permission.POST_NOTIFICATIONS
            ) == PERMISSION_GRANTED
        }
        // For older versions, check if notifications are blocked at system level
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

/**
 * Creates a notification for configured for weather forecast updates
 */
private fun Context.createWeatherForecastNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        WEATHER_FORECAST_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel = NotificationChannel(
        WEATHER_FORECAST_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notification_weather_forecast_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description =
            getString(R.string.core_notification_weather_forecast_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.weatherForecastPendingIntent(location: String): PendingIntent? =
    PendingIntent.getActivity(
        this,
        WEATHER_FORECAST_NOTIFICATION_REQUEST_CODE,
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = location.toDeepLinkUri()
            component = ComponentName(
                packageName,
                TARGET_ACTIVITY_NAME,
            )
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

private fun String.toDeepLinkUri() = "${DEEP_LINK_BASE_PATH}/$this".toUri()