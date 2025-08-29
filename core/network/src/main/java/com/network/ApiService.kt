package com.network

import ForecastDTO
import com.network.models.reponse.currentweather.CurrentWeatherDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json")
    suspend fun fetchCurrentWeather(@Query("q") location: String): Response<CurrentWeatherDTO>


    @GET("forecast.json")
    suspend fun fetchForecast(
        @Query("q") location: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: Boolean = false,
        @Query("alerts") alerts: Boolean = false
    ): Response<ForecastDTO>
}