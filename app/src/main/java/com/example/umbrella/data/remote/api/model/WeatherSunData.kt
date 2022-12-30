package com.example.umbrella.data.remote.api.model

data class WeatherSunData(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)