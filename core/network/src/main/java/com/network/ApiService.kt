package com.network

import com.network.models.reponse.CurrentWeather
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun fetchCurrentWeather(): Response<CurrentWeather>
}