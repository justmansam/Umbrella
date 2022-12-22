package com.example.umbrella.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.api.RetrofitInstance
import com.example.umbrella.api.WeatherDataItem
import com.example.umbrella.sharedPrefImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    /*
    Compose state yerine bunun kullanılmasının sebebi app termination durumuna karşı
    SaveStateHandle ile daha verimli çalışması ve compose free olması (reusable with xml)
     */
    //var tempr: Double by mutableStateOf(0.0) //COMPOSE STATE as an alternative
    // Main UI state
    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()

    fun showApiCallResult(city: String?, latitude: String?, longitude: String?) {
        _mainUiState.update { currentState ->
            currentState.copy(
                isInProcess = true,
                hasLocation = false,
                hasSharedPref = false
            )
        }
        viewModelScope.launch {
            val response = try {
                if (latitude != null && longitude != null && latitude != "null" && longitude != "null") {
                    _mainUiState.update { currentState -> currentState.copy(hasLocation = true) }
                    RetrofitInstance.api.getWeatherByCoordination(
                        latitude,
                        longitude,
                        API_KEY
                    )
                } else {
                    if (city != null) {
                        RetrofitInstance.api.getWeatherByCity(
                            city,
                            API_KEY
                        )
                    } else {
                        RetrofitInstance.api.getWeatherByCity(
                            "",
                            API_KEY
                        )
                    }
                }
            } catch (e: IOException) {
                // CHECK INTERNET
                Log.e("TAGGG ", "IOExeption, check your connection")
                _mainUiState.update { currentState -> currentState.copy(apiHasResponse = false) }
                return@launch
            } catch (e: HttpException) {
                Log.e("TAGGG ", "HttpException, unexpected response")
                _mainUiState.update { currentState -> currentState.copy(apiHasResponse = false) }
                return@launch
            }
            if (response.isSuccessful) {
                getResponses(response)
                _mainUiState.update { currentState ->
                    currentState.copy(
                        apiHasResponse = true,
                        isSearchActive = false,
                        isSearchFailed = false
                    )
                }
            } else {
                // Check for typo for city name you typed!
                _mainUiState.update { currentState ->
                    currentState.copy(
                        apiHasResponse = false,
                        isSearchActive = true,
                        isSearchFailed = true
                    )
                }
                Log.e("TAGGG ", "Check the city name you typed")
            }
        }
        _mainUiState.update { currentState ->
            currentState.copy(
                isInProcess = false
            )
        }
    }

    private fun getResponses(response: Response<WeatherDataItem>) {
        _mainUiState.update { currentState ->
            currentState.copy(
                city = response.body()!!.name,
                currentTemperature = response.body()!!.main.temp.toInt(),
                feelsLikeTemperature = response.body()!!.main.feels_like.toInt(),
                minTemperature = response.body()!!.main.temp_min.toInt(),
                maxTemperature = response.body()!!.main.temp_max.toInt(),
                visibility = response.body()!!.visibility / 100,
                humidity = response.body()!!.main.humidity,
                wind = response.body()!!.wind.speed.toInt(),
                sunrise = calculateLocalTime(response, response.body()!!.sys.sunrise, true),
                sunset = calculateLocalTime(response, response.body()!!.sys.sunset, true),
                lastUpdateTime = calculateLocalTime(response, response.body()!!.dt, false)
            )
        }
        updateSharedPreferences()
    }

    private fun calculateLocalTime(
        response: Response<WeatherDataItem>,
        dateAndTimeInUnix: Int,
        returnOnlyHour: Boolean
    ): String {
        val timeZone = response.body()!!.timezone
        val timeDifference = timeZone / 3600
        val dateAndTimeRaw = java.time.format.DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(dateAndTimeInUnix.toLong()))
        val dateAndTimeStringList = dateAndTimeRaw.split("T")
        val hourAndMinuteStringList = dateAndTimeStringList[1].split(":").dropLast(1)
        val hour = hourAndMinuteStringList[0].toInt() + timeDifference
        val hourString = if (hour < 10) {
            "0"
        } else {
            ""
        } + hour.toString() + ":" + hourAndMinuteStringList[1]
        return if (returnOnlyHour) {
            hourString
        } else {
            val dateString = dateAndTimeStringList[0] + " " + hourString
            dateString
        }
    }

    private fun updateSharedPreferences() {
        viewModelScope.launch {
            sharedPrefImpl.setValue(citySP, mainUiState.value.city)
            sharedPrefImpl.setValue(currentTempSP, mainUiState.value.currentTemperature.toString())
            sharedPrefImpl.setValue(
                feelsLikeTempSP,
                mainUiState.value.feelsLikeTemperature.toString()
            )
            sharedPrefImpl.setValue(minTempSP, mainUiState.value.minTemperature.toString())
            sharedPrefImpl.setValue(maxTempSP, mainUiState.value.maxTemperature.toString())
            sharedPrefImpl.setValue(visibilitySP, mainUiState.value.visibility.toString())
            sharedPrefImpl.setValue(humiditySP, mainUiState.value.humidity.toString())
            sharedPrefImpl.setValue(windSP, mainUiState.value.wind.toString())
            sharedPrefImpl.setValue(sunriseSP, mainUiState.value.sunrise)
            sharedPrefImpl.setValue(sunsetSP, mainUiState.value.sunset)
            sharedPrefImpl.setValue(lastUpdateTimeSP, mainUiState.value.lastUpdateTime)
        }
        _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
    }

    fun searchActivated() {
        _mainUiState.update { currentState -> currentState.copy(isSearchActive = !mainUiState.value.isSearchActive) }
    }

    fun exposeLocalData(
        cityFromMain: String?,
        currentTempFromMain: String?,
        feelsLikeTempFromMain: String?,
        minTempFromMain: String?,
        maxTempFromMain: String?,
        visibilityFromMain: String?,
        humidityFromMain: String?,
        windFromMain: String?,
        sunriseFromMain: String?,
        sunsetFromMain: String?,
        lastUpdateTimeFromMain: String?
    ) {
        _mainUiState.update { currentState ->
            currentState.copy(
                isInProcess = false,
                city = cityFromMain!!,
                currentTemperature = currentTempFromMain!!.toInt(),
                feelsLikeTemperature = feelsLikeTempFromMain!!.toInt(),
                minTemperature = minTempFromMain!!.toInt(),
                maxTemperature = maxTempFromMain!!.toInt(),
                visibility = visibilityFromMain!!.toInt(),
                humidity = humidityFromMain!!.toInt(),
                wind = windFromMain!!.toInt(),
                sunrise = sunriseFromMain!!,
                sunset = sunsetFromMain!!,
                lastUpdateTime = lastUpdateTimeFromMain!!,
                hasSharedPref = true
            )
        }
    }

    companion object {
        private const val API_KEY = "7d9c2f60d1047b2aaae0639fdd393995"

        // FOR Shared Preferences
        const val citySP = "city"
        const val currentTempSP = "currentTemp"
        const val feelsLikeTempSP = "feelsLikeTemp"
        const val minTempSP = "minTemp"
        const val maxTempSP = "maxTemp"
        const val visibilitySP = "visibility"
        const val humiditySP = "humidity"
        const val windSP = "wind"
        const val sunriseSP = "sunrise"
        const val sunsetSP = "sunset"
        const val lastUpdateTimeSP = "lastUpdateTime"
    }
}