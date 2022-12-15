package com.example.umbrella.api

import retrofit2.Response
import retrofit2.http.GET

interface WeatherApi {

    //@GET("/data/2.5/weather?lat=44.34&lon=10.99&appid=7d9c2f60d1047b2aaae0639fdd393995")
    @GET("/data/2.5/weather?q=Uppsala&appid=7d9c2f60d1047b2aaae0639fdd393995")
    suspend fun getWeather(): Response<WeatherDataItem>


}