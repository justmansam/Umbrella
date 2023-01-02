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
            val sharedPrefArray = sharedPrefImpl.getValue(SHARED_PREF_KEY_ARRAY)
            if (!sharedPrefArray[0].isNullOrEmpty()) exposeLocalData(sharedPrefArray)
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
            val uiDataStateArray = arrayOf(
                uiDataState.value.city,
                uiDataState.value.currentTemperature,
                uiDataState.value.feelsLikeTemperature,
                uiDataState.value.minTemperature,
                uiDataState.value.maxTemperature,
                uiDataState.value.visibility,
                uiDataState.value.humidity,
                uiDataState.value.wind,
                uiDataState.value.sunrise,
                uiDataState.value.sunset,
                uiDataState.value.lastUpdateTime,
                uiDataState.value.weatherIcon
            )
            sharedPrefImpl.setValue(SHARED_PREF_KEY_ARRAY, uiDataStateArray)
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
        private val SHARED_PREF_KEY_ARRAY = arrayOf(
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