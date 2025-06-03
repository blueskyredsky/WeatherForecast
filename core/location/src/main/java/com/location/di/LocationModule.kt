package com.location.di

import com.location.DefaultLocationProvider
import com.location.LocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Singleton
    @Binds
    abstract fun bindLocationProvider(
        defaultLocationProvider: DefaultLocationProvider
    ): LocationProvider
}