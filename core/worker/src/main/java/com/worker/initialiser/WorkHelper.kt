package com.worker.initialiser

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType

const val WEATHER_FORECAST_TOPIC = "WeatherForecastTopic"
private const val WORKER_NOTIFICATION_ID = 0
private const val WORKER_NOTIFICATION_CHANNEL_ID = "WorkerNotificationChannel"

// All workers need an internet connection
val WorkerConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()


/**
 * Foreground information for worker on lower API levels when workers are being
 * run with a foreground service
 */
fun Context.workerForegroundInfo() = ForegroundInfo(
    WORKER_NOTIFICATION_ID,
    workerNotification(),
)

/**
 * Notification displayed on lower API levels when workers are being
 * run with a foreground service
 */
private fun Context.workerNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            WORKER_NOTIFICATION_CHANNEL_ID,
            "",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = ""
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        WORKER_NOTIFICATION_CHANNEL_ID,
    )
//        .setSmallIcon()
        .setContentTitle("")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}