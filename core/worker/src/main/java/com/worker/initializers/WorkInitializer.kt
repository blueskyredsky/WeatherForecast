package com.worker.initializers

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.notification.NotificationHandler
import com.worker.WeatherForecastWorker

object WorkInitializer {
    fun initialize(context: Context, notificationHandler: NotificationHandler) {
        if (!notificationHandler.isNotificationsEnabled()) {
            Log.d("WorkInitializer", "Notifications disabled, NOT enqueuing worker.")
            return
        }

        WorkManager.getInstance(context).apply {
            enqueueUniquePeriodicWork(
                WEATHER_FORECAST_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                WeatherForecastWorker.startUpWork(),
            )
        }
    }

    fun cancelWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WEATHER_FORECAST_WORKER_NAME)
    }
}

internal const val WEATHER_FORECAST_WORKER_NAME = "WeatherForecastWorker"