package com.notification.di

import com.notification.DefaultNotificationHandler
import com.notification.NotificationHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotificationHandler(
        handler: DefaultNotificationHandler
    ): NotificationHandler
}