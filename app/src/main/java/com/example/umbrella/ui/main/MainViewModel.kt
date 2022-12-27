package com.example.umbrella.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.data.local.pref.SharedPreferencesImpl
import com.example.umbrella.data.remote.api.RetrofitInstance
import com.example.umbrella.data.remote.api.WeatherDataItem
import com.example.umbrella.sharedPrefImpl
import com.example.umbrella.ui.common.toUTCformatedLocalTime
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

    // Main UI state
    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()

    fun lookForSharedPref() {
        sharedPrefImpl = SharedPreferencesImpl(com.example.umbrella.sharedPref)
        viewModelScope.launch {
            val sharedPrefArray = arrayOf(
                sharedPrefImpl.getValue(citySP),
                sharedPrefImpl.getValue(currentTempSP),
                sharedPrefImpl.getValue(feelsLikeTempSP),
                sharedPrefImpl.getValue(minTempSP),
                sharedPrefImpl.getValue(maxTempSP),
                sharedPrefImpl.getValue(visibilitySP),
                sharedPrefImpl.getValue(humiditySP),
                sharedPrefImpl.getValue(windSP),
                sharedPrefImpl.getValue(sunriseSP),
                sharedPrefImpl.getValue(sunsetSP),
                sharedPrefImpl.getValue(lastUpdateTimeSP),
                sharedPrefImpl.getValue(weatherIconSP)
            )
            if (!sharedPrefArray[0].isNullOrEmpty()) {
                exposeLocalData(sharedPrefArray)
            }
        }
    }

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
                _mainUiState.update { currentState ->
                    currentState.copy(
                        isSearchFailed = 2,
                        isSearchActive = true,
                        apiHasResponse = false,
                        isInProcess = false
                    )
                }
                return@launch
            } catch (e: HttpException) {
                _mainUiState.update { currentState ->
                    currentState.copy(
                        isSearchFailed = 3,
                        isSearchActive = true,
                        apiHasResponse = false,
                        isInProcess = false
                    )
                }
                return@launch
            }
            if (response.isSuccessful) {
                getResponses(response)
                _mainUiState.update { currentState ->
                    currentState.copy(
                        apiHasResponse = true,
                        isSearchActive = false,
                        isSearchFailed = 0
                    )
                }
            } else {
                _mainUiState.update { currentState ->
                    currentState.copy(
                        isSearchFailed = 1,
                        isSearchActive = true,
                        isInProcess = false,
                        apiHasResponse = false,
                    )
                }
            }
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
                sunrise = (response.body()!!.sys.sunrise).toUTCformatedLocalTime(response, true),
                sunset = (response.body()!!.sys.sunset).toUTCformatedLocalTime(response, true),
                lastUpdateTime = (response.body()!!.dt).toUTCformatedLocalTime(response, false),
                weatherIcon = response.body()!!.weather[0].icon
            )
        }
        _mainUiState.update { currentState ->
            currentState.copy(
                isInProcess = false
            )
        }
        updateSharedPreferences()
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
            sharedPrefImpl.setValue(weatherIconSP, mainUiState.value.weatherIcon)
            _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
        }
    }

    private fun exposeLocalData(sharedPrefDataToExpose: Array<String?>) {
        _mainUiState.update { currentState ->
            currentState.copy(
                city = sharedPrefDataToExpose[0]!!,
                currentTemperature = sharedPrefDataToExpose[1]!!.toInt(),
                feelsLikeTemperature = sharedPrefDataToExpose[2]!!.toInt(),
                minTemperature = sharedPrefDataToExpose[3]!!.toInt(),
                maxTemperature = sharedPrefDataToExpose[4]!!.toInt(),
                visibility = sharedPrefDataToExpose[5]!!.toInt(),
                humidity = sharedPrefDataToExpose[6]!!.toInt(),
                wind = sharedPrefDataToExpose[7]!!.toInt(),
                sunrise = sharedPrefDataToExpose[8]!!,
                sunset = sharedPrefDataToExpose[9]!!,
                lastUpdateTime = sharedPrefDataToExpose[10]!!,
                weatherIcon = sharedPrefDataToExpose[11]!!,
                hasSharedPref = true
            )
        }
    }

    fun searchActivated() {
        _mainUiState.update { currentState -> currentState.copy(isSearchActive = !mainUiState.value.isSearchActive) }
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
        const val weatherIconSP = "weatherIcon"
    }
}