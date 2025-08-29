package com.currentweather.di

import com.currentweather.data.repository.WeatherRepository
import com.currentweather.data.repository.DefaultWeatherRepository
import com.currentweather.data.repository.DefaultLocationRepository
import com.currentweather.data.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class CurrentWeatherModule {

    @Binds
    @Reusable
    abstract fun bindCurrentWeatherRepository(
        defaultCurrentWeatherRepository: DefaultWeatherRepository
    ): WeatherRepository

    @Binds
    @Reusable
    abstract fun bindLocationRepository(
        defaultLocationRepository: DefaultLocationRepository
    ): LocationRepository
}