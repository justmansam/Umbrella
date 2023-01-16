package com.example.umbrella.ui.main

import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.connectivityManager
import com.example.umbrella.data.common.RepositoryImpl
import com.example.umbrella.data.common.RepositoryImpl.Companion.SHARED_PREF_KEY_ARRAY
import com.example.umbrella.data.remote.api.RetrofitInstance.api
import com.example.umbrella.fusedLocationClient
import com.example.umbrella.sharedPref
import com.example.umbrella.ui.main.model.MainUiState
import com.example.umbrella.ui.main.model.UiDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RepositoryImpl = RepositoryImpl(api, sharedPref)
) : ViewModel() {
    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()
    private val _uiDataState = MutableStateFlow(UiDataState())
    val uiDataState: StateFlow<UiDataState> = _uiDataState.asStateFlow()

    init {
        lookForSharedPreferences()
    }

    private fun lookForSharedPreferences() {
        viewModelScope.launch {
            val sharedPrefArray = repository.getSharedPref(SHARED_PREF_KEY_ARRAY)
            if (sharedPrefArray[0] != null && sharedPrefArray[1] != null && sharedPrefArray[11] != null) {
                updateUiDataState(sharedPrefArray)
                _mainUiState.update { currentState -> currentState.copy(hasSharedPref = true) }
            }
        }
    }

    @RequiresPermission("android.permission.ACCESS_COARSE_LOCATION")
    fun getLocation() {
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
                repository.getRemoteWeatherData(city, latitude, longitude)
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
            updateMainUiState(response)
        }
    }

    private fun updateMainUiState(response: Array<String?>) {
        if (response.isNotEmpty() && !response[0].equals("nullPointerException")) {
            updateUiDataState(response)
            _mainUiState.update { currentState ->
                currentState.copy(
                    hasSharedPref = true,
                    isSearchActive = false,
                    isSearchFailed = 0
                )
            }
        } else if (response.isEmpty()) {
            _mainUiState.update { currentState ->
                currentState.copy(
                    isSearchFailed = 1,
                    isSearchActive = true,
                    isInProcess = false
                )
            }
        } else { // If has nullPointerException
            _mainUiState.update { currentState ->
                currentState.copy(
                    isSearchFailed = 3,
                    isSearchActive = true,
                    isInProcess = false
                )
            }
        }
    }

    private fun updateUiDataState(dataToExpose: Array<String?>) {
        _uiDataState.update { currentState ->
            currentState.copy(
                city = dataToExpose[0] ?: "--",
                currentTemperature = dataToExpose[1] ?: "--",
                feelsLikeTemperature = dataToExpose[2] ?: "--",
                minTemperature = dataToExpose[3] ?: "--",
                maxTemperature = dataToExpose[4] ?: "--",
                visibility = dataToExpose[5] ?: "--",
                humidity = dataToExpose[6] ?: "--",
                wind = dataToExpose[7] ?: "--",
                sunrise = dataToExpose[8] ?: "--",
                sunset = dataToExpose[9] ?: "--",
                lastUpdateTime = dataToExpose[10] ?: "--",
                weatherIcon = dataToExpose[11] ?: "--"
            )
        }
        _mainUiState.update { currentState -> currentState.copy(isInProcess = false) }
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

    fun searchActivated() {
        _mainUiState.update { currentState -> currentState.copy(isSearchActive = !mainUiState.value.isSearchActive) }
    }

    fun refreshActivated() {
        _mainUiState.update { currentState -> currentState.copy(isRefreshing = !mainUiState.value.isRefreshing) }
    }
}