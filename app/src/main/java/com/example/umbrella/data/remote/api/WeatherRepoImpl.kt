package com.example.umbrella.data.remote.api

import com.example.umbrella.data.remote.api.model.WeatherData
import com.example.umbrella.ui.main.MainViewModel
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
                MainViewModel.API_KEY
            )
        } else {
            if (city != null) {
                api.getWeatherByCity(
                    city,
                    MainViewModel.API_KEY
                )
            } else {
                api.getWeatherByCity(
                    "",
                    MainViewModel.API_KEY
                )
            }
        }
    }

}