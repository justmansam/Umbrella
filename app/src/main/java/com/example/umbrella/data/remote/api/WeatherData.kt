package com.example.umbrella.data.remote.api

data class WeatherData(
    val dt: Int,
    val main: WeatherMainEventsData,
    val name: String,
    val sys: WeatherSunData,
    val timezone: Int,
    val visibility: Int,
    val weather: List<WeatherGeneralData>,
    val wind: WeatherWindData
)