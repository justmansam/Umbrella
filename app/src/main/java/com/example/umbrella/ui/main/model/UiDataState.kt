package com.example.umbrella.ui.main.model

data class UiDataState(
    val city: String = "- - -",
    val currentTemperature: String = "-",
    val feelsLikeTemperature: String = "-",
    val minTemperature: String = "-",
    val maxTemperature: String = "-",
    val visibility: String = "-",
    val humidity: String = "-",
    val wind: String = "-",
    val sunrise: String = "--:--",
    val sunset: String = "--:--",
    val lastUpdateTime: String = "--:--",
    val weatherIcon: String = "*"
)
