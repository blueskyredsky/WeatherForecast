package com.weatherforcast

import android.app.Application
import com.worker.initializers.WorkInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        WorkInitializer.initialize(this)
    }
}