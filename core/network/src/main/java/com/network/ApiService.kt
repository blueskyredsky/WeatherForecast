package com.network

import com.network.models.reponse.CurrentWeatherDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json")
    suspend fun fetchCurrentWeather(@Query("q") location: String): Response<CurrentWeatherDTO>
}