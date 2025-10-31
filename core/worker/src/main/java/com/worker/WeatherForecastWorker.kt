package com.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.datastore.UserPreferenceManager
import com.network.ApiService
import com.network.models.reponse.currentweather.CurrentWeatherDTO
import com.notification.NotificationHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.roundToInt

@HiltWorker
class WeatherForecastWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val userPreferenceManager: UserPreferenceManager,
    private val notificationHandler: NotificationHandler
) : CoroutineWorker(appContext, workerParams) {

    private companion object {
        private const val TAG = "WeatherForecastWorker"
    }

    override suspend fun doWork(): Result {
        val coordinates = userPreferenceManager.userCoordinatesFlow.firstOrNull()

        if (coordinates?.latitude == null || coordinates.longitude == null) {
            Log.e(TAG,"No user coordinates found to fetch weather.")
            return Result.failure()
        }

        val location = "${coordinates.latitude},${coordinates.longitude}"
        return try {
            val response = apiService.fetchCurrentWeather(location = location)
            if (response.isSuccessful && response.body() != null) {
                val currentWeatherDTO = response.body()!!

                val (city, weatherSummary) = processCurrentWeather(currentWeatherDTO)

                notificationHandler.postWeatherForecastNotification(
                    location = city,
                    weatherForecast = weatherSummary
                )
                Result.success()
            } else {
                Log.e(TAG,"Weather API failed with code: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather forecast. $e")
            Result.retry()
        }
    }

    private fun processCurrentWeather(currentWeatherDTO: CurrentWeatherDTO): Pair<String, String> {
        // Implement logic to extract summary, e.g., "Max temp 15°C, Min 5°C, Sunny."
        val city = currentWeatherDTO.locationDTO?.name.orEmpty()
        val currentTemperature = "${currentWeatherDTO.currentDTO?.tempC?.roundToInt()?.toString().orEmpty() }°"
        val currentCondition = currentWeatherDTO.currentDTO?.conditionDTO?.text.orEmpty()

        return city to "$currentTemperature, $currentCondition"
    }
}