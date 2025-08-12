package com.forecaset.di

import com.forecaset.data.repository.DefaultForecastRepository
import com.forecaset.data.repository.DefaultLocationRepository
import com.forecaset.data.repository.ForecastRepository
import com.forecaset.data.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ForecastModule {

    @Binds
    @Reusable
    abstract fun bindForecastRepository(
        defaultForecastRepository: DefaultForecastRepository
    ): ForecastRepository

    @Binds
    @Reusable
    abstract fun bindLocationRepository(
        defaultLocationRepository: DefaultLocationRepository
    ): LocationRepository
}