package com.example.umbrella.data.common

import android.content.SharedPreferences
import com.example.umbrella.common.toUTCformatedLocalTime
import com.example.umbrella.data.local.pref.ISharedPreferences
import com.example.umbrella.data.remote.api.IWeatherApi
import com.example.umbrella.data.remote.api.IWeatherRepo
import com.example.umbrella.data.remote.api.model.WeatherData
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: IWeatherApi,
    private val pref: SharedPreferences
) : IWeatherRepo, ISharedPreferences {

    override suspend fun getRemoteWeatherData(
        city: String?,
        latitude: String?,
        longitude: String?
    ): Array<String?> {
        val apiResponse = if (latitude != null && longitude != null) {
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
        return if (apiResponse.isSuccessful) {
            try {
                val responseStrArray = convertResponseAndUpdateSharedPref(apiResponse)
                responseStrArray
            } catch (e: java.lang.NullPointerException) {
                arrayOf("nullPointerException")
            }
        } else {
            emptyArray()
        }
    }

    override suspend fun convertResponseAndUpdateSharedPref(response: Response<WeatherData>): Array<String?> {
        val apiResponseArray: Array<String?> = arrayOf(
            response.body()!!.name,
            response.body()!!.main.temp.toInt().toString(),
            response.body()!!.main.feels_like.toInt().toString(),
            response.body()!!.main.temp_min.toInt().toString(),
            response.body()!!.main.temp_max.toInt().toString(),
            (response.body()!!.visibility / 100).toString(),
            response.body()!!.main.humidity.toString(),
            response.body()!!.wind.speed.toInt().toString(),
            (response.body()!!.sys.sunrise).toUTCformatedLocalTime(response, true),
            (response.body()!!.sys.sunset).toUTCformatedLocalTime(response, true),
            (response.body()!!.dt).toUTCformatedLocalTime(response, false),
            response.body()!!.weather[0].icon
        )
        setSharedPref(SHARED_PREF_KEY_ARRAY, apiResponseArray)
        return apiResponseArray
    }

    override suspend fun setSharedPref(key: Array<String>, value: Array<String?>) {
        for (i in key) {
            pref.edit().putString(i, value[key.indexOf(i)]).apply()
        }
    }

    override suspend fun getSharedPref(key: Array<String>): Array<String?> {
        var arrayOfSharedPref: Array<String?> = emptyArray()
        for (i in key) {
            arrayOfSharedPref += pref.getString(i, null)
        }
        return arrayOfSharedPref
    }

    override suspend fun removeSharedPref(key: Array<String>) {
        for (i in key) {
            pref.edit().remove(i).apply()
        }
    }

    companion object {
        private const val API_KEY = "7d9c2f60d1047b2aaae0639fdd393995"
        val SHARED_PREF_KEY_ARRAY = arrayOf(
            "city",
            "currentTemp",
            "feelsLikeTemp",
            "minTemp",
            "maxTemp",
            "visibility",
            "humidity",
            "wind",
            "sunrise",
            "sunset",
            "lastUpdateTime",
            "weatherIcon"
        )
    }
}