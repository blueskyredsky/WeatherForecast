package com.weatherforcast

import android.app.Application
import com.worker.initializers.WeatherForecastWorkInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        WeatherForecastWorkInitializer.initialise(this)
    }
}