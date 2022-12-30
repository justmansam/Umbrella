package com.example.umbrella.data.remote.api

import com.example.umbrella.data.remote.api.model.WeatherData
import retrofit2.Response

interface IWeatherRepo {
    suspend fun getWeatherData(
        city: String? = null,
        latitude: String? = null,
        longitude: String? = null
    ): Response<WeatherData>
}