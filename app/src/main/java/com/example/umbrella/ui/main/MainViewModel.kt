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
        sharedPrefImpl = SharedPreferencesImpl(com.example.umbrella.sharedPref)
        lookForSharedPreferences()
    }

    private fun lookForSharedPreferences() {
        viewModelScope.launch {
            val sharedPrefArray = sharedPrefImpl.getValue(SHARED_PREF_KEY_ARRAY)
            if (!sharedPrefArray[0].isNullOrEmpty()) {
                updateUiDataState(sharedPrefArray)
                _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
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
                updateSharedPreferences(response)
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

    private suspend fun updateSharedPreferences(response: Response<WeatherData>) {
        viewModelScope.launch {
            val apiResponseArray = arrayOf(
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
            sharedPrefImpl.setValue(SHARED_PREF_KEY_ARRAY, apiResponseArray)
            _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
            lookForSharedPreferences()
        }
    }

    private fun updateUiDataState(dataToExpose: Array<String?>) {
        _uiDataState.update { currentState ->
            currentState.copy(
                city = dataToExpose[0]!!,
                currentTemperature = dataToExpose[1]!!,
                feelsLikeTemperature = dataToExpose[2]!!,
                minTemperature = dataToExpose[3]!!,
                maxTemperature = dataToExpose[4]!!,
                visibility = dataToExpose[5]!!,
                humidity = dataToExpose[6]!!,
                wind = dataToExpose[7]!!,
                sunrise = dataToExpose[8]!!,
                sunset = dataToExpose[9]!!,
                lastUpdateTime = dataToExpose[10]!!,
                weatherIcon = dataToExpose[11]!!
            )
        }
        _mainUiState.update { currentState -> currentState.copy(isInProcess = false) }
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