package com.example.umbrella.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather?units=metric")
    suspend fun getWeatherByCity(
        @Query(value = "q") city: String,
        @Query(value = "appid") api_key: String
    ): Response<WeatherDataItem>

    @GET("weather?units=metric")
    suspend fun getWeatherByCoordination(
        @Query(value = "lat") latitude: String,
        @Query(value = "lon") longitude: String,
        @Query(value = "appid") api_key: String
    ): Response<WeatherDataItem>

}