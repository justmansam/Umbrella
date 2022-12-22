package com.example.umbrella.ui

data class MainUiState(
    // FOR GENERAL UI STATE MANAGEMENT
    val apiHasResponse: Boolean = false,
    val hasLocation: Boolean = false,
    val isSearchActive: Boolean = false,
    val isSearchFailed: Boolean = false,
    val hasSharedPref: Boolean = false,
    // FOR DATA UPDATE (according to CITY or LOCATION api RESPONSE)
    val city: String = "",
    //val latitude: String = "",
    //val longitude: String = "",
    val currentTemperature: Int = 0,
    val feelsLikeTemperature: Int = 0,
    val minTemperature: Int = 0,
    val maxTemperature: Int = 0,
    val visibility: Int = 0,
    val humidity: Int = 0,
    val wind: Int = 0,
    val sunrise: String = "",
    val sunset: String = "",
    val lastUpdateTime: String = ""
)
