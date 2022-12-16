package com.example.umbrella.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    //@GET("/data/2.5/weather?lat=44.34&lon=10.99&appid=7d9c2f60d1047b2aaae0639fdd393995")
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query(value = "q") city: String,
        @Query(value = "appid") api_key: String
    ): Response<WeatherDataItem>

}