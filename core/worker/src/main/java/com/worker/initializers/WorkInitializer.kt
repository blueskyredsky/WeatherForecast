package com.worker.initializers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.worker.WeatherForecastWorker

object WorkInitializer {
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            enqueueUniquePeriodicWork(
                WEATHER_FORECAST_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                WeatherForecastWorker.startUpWork(),
            )
        }
    }
}

internal const val WEATHER_FORECAST_WORKER_NAME = "WeatherForecastWorker"