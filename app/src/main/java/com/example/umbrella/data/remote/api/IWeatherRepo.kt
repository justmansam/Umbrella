package com.example.umbrella.data.remote.api

interface IWeatherRepo {
    suspend fun getRemoteWeatherData(
        city: String? = null,
        latitude: String? = null,
        longitude: String? = null
    ): Array<String?>
}