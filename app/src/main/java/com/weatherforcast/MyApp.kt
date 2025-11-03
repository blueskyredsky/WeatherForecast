package com.weatherforcast

import android.app.Application
import com.worker.initialiser.WeatherForecastWorkInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        WeatherForecastWorkInitializer.initialise(this)
    }
}