package com.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationHandler {
    private val CHANNEL_ID = "weather_updates_channel"
    private val NOTIFICATION_ID = 101

    private val MAIN_ACTIVITY_CLASS_NAME = "com.weatherforcast.app.MainActivity"

    override fun showWeatherNotification(title: String, content: String) {
        createNotificationChannel() // Call without context

        // Intent to launch MainActivity, which will handle navigation to the weather screen
        val intent = Intent(context, Class.forName(MAIN_ACTIVITY_CLASS_NAME)).apply { // Use injected context
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            pendingIntentFlags
        )

//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_ic_menu_today)
//            .setContentTitle(title)
//            .setContentText(content)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        with(NotificationManagerCompat.from(context)) { // Use injected context
//            // Check for notification permission if targeting Android 13+ (API 33+)
//            notify(NOTIFICATION_ID, builder.build())
//        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Updates"
            val descriptionText = "Shows current weather conditions twice daily."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // Use injected context
            notificationManager.createNotificationChannel(channel)
        }
    }
}