package com.datastore.di

import com.datastore.DataStoreUserPreferenceManager
import com.datastore.UserPreferenceManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferenceManager(
        manager: DataStoreUserPreferenceManager
    ): UserPreferenceManager
}