package com.worker.status

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.worker.WeatherForecastWorker
import com.worker.initializers.WEATHER_FORECAST_WORKER_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultWorkStatusManager @Inject constructor(@param:ApplicationContext private val context: Context): WorkStatusManager {
    override val isRunning: Flow<Boolean>
         = WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(WEATHER_FORECAST_WORKER_NAME)
        .map(List<WorkInfo>::anyRunning)
        .conflate()

    override fun run() {
        val workManager = WorkManager.getInstance(context)
        // Run worker
        workManager.enqueueUniquePeriodicWork(
            WEATHER_FORECAST_WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            WeatherForecastWorker.startUpWork(),
        )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WEATHER_FORECAST_WORKER_NAME)
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }