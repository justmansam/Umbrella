package com.example.umbrella.ui.main

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.connectivityManager
import com.example.umbrella.data.local.pref.SharedPreferencesImpl
import com.example.umbrella.data.remote.api.RetrofitInstance.api
import com.example.umbrella.data.remote.api.WeatherRepoImpl
import com.example.umbrella.data.remote.api.model.WeatherData
import com.example.umbrella.fusedLocationClient
import com.example.umbrella.sharedPrefImpl
import com.example.umbrella.ui.common.toUTCformatedLocalTime
import com.example.umbrella.ui.main.model.MainUiState
import com.example.umbrella.ui.main.model.UiDataState
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
    private val repository = WeatherRepoImpl(api)
    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()
    private val _uiDataState = MutableStateFlow(UiDataState())
    val uiDataState: StateFlow<UiDataState> = _uiDataState.asStateFlow()

    init {
        lookForSharedPreferences()
    }

    private fun lookForSharedPreferences() {
        sharedPrefImpl = SharedPreferencesImpl(com.example.umbrella.sharedPref)
        viewModelScope.launch {
            val sharedPrefArray = arrayOf(
                sharedPrefImpl.getValue(CITY_SP),
                sharedPrefImpl.getValue(TEMPERATURE_SP),
                sharedPrefImpl.getValue(FEELS_LIKE_SP),
                sharedPrefImpl.getValue(MIN_TEMP_SP),
                sharedPrefImpl.getValue(MAX_TEMP_SP),
                sharedPrefImpl.getValue(VISIBILITY_SP),
                sharedPrefImpl.getValue(HUMIDITY_SP),
                sharedPrefImpl.getValue(WIND_SP),
                sharedPrefImpl.getValue(SUN_RISE_SP),
                sharedPrefImpl.getValue(SUN_SET_SP),
                sharedPrefImpl.getValue(UPDATE_TIME_SP),
                sharedPrefImpl.getValue(WEATHER_ICON_SP)
            )
            if (!sharedPrefArray[0].isNullOrEmpty()) {
                exposeLocalData(sharedPrefArray)
            }
        }
    }

    fun getLocation() {
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

    fun callApiForResult(city: String?, latitude: String?, longitude: String?) {
        viewModelScope.launch {
            _mainUiState.update { currentState -> currentState.copy(isInProcess = true) }
            val response = try {
                repository.getWeatherData(city, latitude, longitude)
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
        _uiDataState.update { currentState ->
            currentState.copy(
                city = response.body()!!.name,
                currentTemperature = response.body()!!.main.temp.toInt().toString(),
                feelsLikeTemperature = response.body()!!.main.feels_like.toInt().toString(),
                minTemperature = response.body()!!.main.temp_min.toInt().toString(),
                maxTemperature = response.body()!!.main.temp_max.toInt().toString(),
                visibility = (response.body()!!.visibility / 100).toString(),
                humidity = response.body()!!.main.humidity.toString(),
                wind = response.body()!!.wind.speed.toInt().toString(),
                sunrise = (response.body()!!.sys.sunrise).toUTCformatedLocalTime(response, true),
                sunset = (response.body()!!.sys.sunset).toUTCformatedLocalTime(response, true),
                lastUpdateTime = (response.body()!!.dt).toUTCformatedLocalTime(response, false),
                weatherIcon = response.body()!!.weather[0].icon
            )
        }
        _mainUiState.update { currentState -> currentState.copy(isInProcess = false) }
        updateSharedPreferences()
    }

    private fun updateSharedPreferences() {
        viewModelScope.launch {
            sharedPrefImpl.setValue(CITY_SP, uiDataState.value.city)
            sharedPrefImpl.setValue(TEMPERATURE_SP, uiDataState.value.currentTemperature)
            sharedPrefImpl.setValue(FEELS_LIKE_SP, uiDataState.value.feelsLikeTemperature)
            sharedPrefImpl.setValue(MIN_TEMP_SP, uiDataState.value.minTemperature)
            sharedPrefImpl.setValue(MAX_TEMP_SP, uiDataState.value.maxTemperature)
            sharedPrefImpl.setValue(VISIBILITY_SP, uiDataState.value.visibility)
            sharedPrefImpl.setValue(HUMIDITY_SP, uiDataState.value.humidity)
            sharedPrefImpl.setValue(WIND_SP, uiDataState.value.wind)
            sharedPrefImpl.setValue(SUN_RISE_SP, uiDataState.value.sunrise)
            sharedPrefImpl.setValue(SUN_SET_SP, uiDataState.value.sunset)
            sharedPrefImpl.setValue(UPDATE_TIME_SP, uiDataState.value.lastUpdateTime)
            sharedPrefImpl.setValue(WEATHER_ICON_SP, uiDataState.value.weatherIcon)
            _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
        }
    }

    private fun exposeLocalData(sharedPrefDataToExpose: Array<String?>) {
        _uiDataState.update { currentState ->
            currentState.copy(
                city = sharedPrefDataToExpose[0]!!,
                currentTemperature = sharedPrefDataToExpose[1]!!,
                feelsLikeTemperature = sharedPrefDataToExpose[2]!!,
                minTemperature = sharedPrefDataToExpose[3]!!,
                maxTemperature = sharedPrefDataToExpose[4]!!,
                visibility = sharedPrefDataToExpose[5]!!,
                humidity = sharedPrefDataToExpose[6]!!,
                wind = sharedPrefDataToExpose[7]!!,
                sunrise = sharedPrefDataToExpose[8]!!,
                sunset = sharedPrefDataToExpose[9]!!,
                lastUpdateTime = sharedPrefDataToExpose[10]!!,
                weatherIcon = sharedPrefDataToExpose[11]!!
            )
        }
        _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
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
        private const val CITY_SP = "city"
        private const val TEMPERATURE_SP = "currentTemp"
        private const val FEELS_LIKE_SP = "feelsLikeTemp"
        private const val MIN_TEMP_SP = "minTemp"
        private const val MAX_TEMP_SP = "maxTemp"
        private const val VISIBILITY_SP = "visibility"
        private const val HUMIDITY_SP = "humidity"
        private const val WIND_SP = "wind"
        private const val SUN_RISE_SP = "sunrise"
        private const val SUN_SET_SP = "sunset"
        private const val UPDATE_TIME_SP = "lastUpdateTime"
        private const val WEATHER_ICON_SP = "weatherIcon"
    }
}