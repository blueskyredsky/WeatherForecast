package com.worker.initialiser

import android.content.Context

object WeatherForecastWorkInitializer {
    fun initialise(context: Context) {
//        WorkManager.getInstance(context).apply {
//            enqueueUniquePeriodicWork(
//                WORK_NAME,
//                ExistingPeriodicWorkPolicy.KEEP,
//                WeatherForecastWorker.startUpWork(),
//            )
//        }
    }
}

internal const val WORK_NAME = "WorkName"