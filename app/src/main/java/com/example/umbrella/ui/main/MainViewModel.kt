package com.example.umbrella.ui.main

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.connectivityManager
import com.example.umbrella.data.local.pref.SharedPreferencesImpl
import com.example.umbrella.data.remote.api.RetrofitInstance
import com.example.umbrella.data.remote.api.WeatherData
import com.example.umbrella.fusedLocationClient
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

    fun getLocation() {
        viewModelScope.launch {
            // Ignore warning!! Permission already checked!!
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        callApiForResult(
                            null,
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }
                }
        }
    }

    fun callApiForResult(city: String?, latitude: String?, longitude: String?) {
        viewModelScope.launch {
            _mainUiState.update { currentState -> currentState.copy(isInProcess = true) }
            val response = try {
                if (latitude != null && longitude != null) {
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
                        isInProcess = false
                    )
                }
                return@launch
            } catch (e: HttpException) {
                _mainUiState.update { currentState ->
                    currentState.copy(
                        isSearchFailed = 3,
                        isSearchActive = true,
                        isInProcess = false
                    )
                }
                return@launch
            }
            if (response.isSuccessful) {
                updateUiState(response)
                _mainUiState.update { currentState ->
                    currentState.copy(
                        isSearchActive = false,
                        isSearchFailed = 0
                    )
                }
            } else {
                _mainUiState.update { currentState ->
                    currentState.copy(
                        isSearchFailed = 1,
                        isSearchActive = true,
                        isInProcess = false
                    )
                }
            }
        }
    }

    private fun updateUiState(response: Response<WeatherData>) {
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

    fun checkConnection() {
        viewModelScope.launch {
            val currentNetwork = connectivityManager?.activeNetwork
            if (currentNetwork == null) {
                _mainUiState.update { currentState -> currentState.copy(hasConnection = false) }
            } else {
                _mainUiState.update { currentState -> currentState.copy(hasConnection = true) }
            }
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
        const val weatherIconSP = "weatherIcon"
    }
}