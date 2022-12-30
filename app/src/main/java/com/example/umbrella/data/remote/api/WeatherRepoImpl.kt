package com.example.umbrella.data.remote.api

import com.example.umbrella.data.remote.api.model.WeatherData
import retrofit2.Response
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val api: IWeatherApi
) : IWeatherRepo {

    override suspend fun getWeatherData(
        city: String?,
        latitude: String?,
        longitude: String?
    ): Response<WeatherData> {
        return if (latitude != null && longitude != null) {
            api.getWeatherByCoordination(
                latitude,
                longitude,
                API_KEY
            )
        } else {
            if (city != null) {
                api.getWeatherByCity(
                    city,
                    API_KEY
                )
            } else {
                api.getWeatherByCity(
                    "",
                    API_KEY
                )
            }
        }
    }

    companion object {
        private const val API_KEY = "7d9c2f60d1047b2aaae0639fdd393995"
    }
}