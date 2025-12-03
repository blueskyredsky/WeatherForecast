package com.worker.di

import com.worker.status.DefaultWorkStatusManager
import com.worker.status.WorkStatusManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkModule {
    @Binds
    internal abstract fun bindWorkStatusManager(
        defaultWorkStatusManager: DefaultWorkStatusManager,
    ): WorkStatusManager
}