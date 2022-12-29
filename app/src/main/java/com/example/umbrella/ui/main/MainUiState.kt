package com.example.umbrella.ui.main

data class MainUiState(
    // FOR GENERAL UI STATE MANAGEMENT
    val apiHasResponse: Boolean = false,
    val hasLocation: Boolean = false,
    val isSearchActive: Boolean = false,
    val isSearchFailed: Int = 0, // 0:No, 1:Yes(Typo!), 2:Yes(No Connection!), 3:Yes(Unexpected!)
    val hasSharedPref: Boolean = false,
    val isInProcess: Boolean = false,
    val hasConnection: Boolean = true,
    // FOR DATA UPDATE (according to CITY or LOCATION api RESPONSE)
    val city: String = "- - -",
    //val latitude: String = "",
    //val longitude: String = "",
    val currentTemperature: Int = 0,
    val feelsLikeTemperature: Int = 0,
    val minTemperature: Int = 0,
    val maxTemperature: Int = 0,
    val visibility: Int = 0,
    val humidity: Int = 0,
    val wind: Int = 0,
    val sunrise: String = "00:00",
    val sunset: String = "00:00",
    val lastUpdateTime: String = "00:00",
    val weatherIcon: String = "*"
)