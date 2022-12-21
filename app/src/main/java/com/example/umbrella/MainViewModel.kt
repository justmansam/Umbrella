package com.example.umbrella

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.api.RetrofitInstance
import com.example.umbrella.api.WeatherDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    // FOR GENERAL UI STATE MANAGEMENT
    private val _apiHasResponse: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val apiHasResponse = _apiHasResponse.asStateFlow() //STATE FLOW
    private val _hasLocation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasLocation = _hasLocation.asStateFlow() //STATE FLOW
    private val _isSearchActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()
    private val _isSearchFailed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearchFailed = _isSearchFailed.asStateFlow()
    private val _hasSharedPref: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasSharedPref = _hasSharedPref.asStateFlow()

    // FROM SEARCH (CITY or LOCATION)
    private val _city: MutableStateFlow<String> = MutableStateFlow("")
    val city = _city.asStateFlow()
    /*
    private val _latitude: MutableStateFlow<String> = MutableStateFlow("")
    val latitude = _latitude.asStateFlow()
    private val _longitude: MutableStateFlow<String> = MutableStateFlow("")
    val longitude = _longitude.asStateFlow()

     */

    // FROM API RESPONSE
    private val _currentTemp: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentTemp = _currentTemp.asStateFlow()
    private val _feelsLikeTemp: MutableStateFlow<Int> = MutableStateFlow(0)
    val feelsLikeTemp = _feelsLikeTemp.asStateFlow()
    private val _minTemp: MutableStateFlow<Int> = MutableStateFlow(0)
    val minTemp = _minTemp.asStateFlow()
    private val _maxTemp: MutableStateFlow<Int> = MutableStateFlow(0)
    val maxTemp = _maxTemp.asStateFlow()
    private val _visibility: MutableStateFlow<Int> = MutableStateFlow(0)
    val visibility = _visibility.asStateFlow()
    private val _humidity: MutableStateFlow<Int> = MutableStateFlow(0)
    val humidity = _humidity.asStateFlow()
    private val _wind: MutableStateFlow<Int> = MutableStateFlow(0)
    val wind = _wind.asStateFlow()
    private val _sunrise: MutableStateFlow<String> = MutableStateFlow("")
    val sunrise = _sunrise.asStateFlow()
    private val _sunset: MutableStateFlow<String> = MutableStateFlow("")
    val sunset = _sunset.asStateFlow()
    private val _lastUpdateTime: MutableStateFlow<String> = MutableStateFlow("")
    val lastUpdateTime = _lastUpdateTime.asStateFlow()

    fun showApiCallResult(city: String?, latitude: String?, longitude: String?) {
        _hasLocation.value = false
        _hasSharedPref.value = false
        viewModelScope.launch {
            val response = try {
                if (latitude != null && longitude != null && latitude != "null" && longitude != "null") {
                    _hasLocation.value = true
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
                _apiHasResponse.value = false
                return@launch
            } catch (e: HttpException) {
                Log.e("TAGGG ", "HttpException, unexpected response")
                _apiHasResponse.value = false
                return@launch
            }
            if (response.isSuccessful) {
                _apiHasResponse.value = true
                _isSearchActive.value = false
                _isSearchFailed.value = false
                getResponses(response)
            } else {
                // Check for typo for city name you typed!
                _apiHasResponse.value = false
                _isSearchActive.value = true
                _isSearchFailed.value = true
                Log.e("TAGGG ", "Check the city name you typed")
            }
        }
    }

    private fun getResponses(response: Response<WeatherDataItem>) {
        _city.value = response.body()!!.name
        _currentTemp.value = response.body()!!.main.temp.toInt()
        _feelsLikeTemp.value = response.body()!!.main.feels_like.toInt()
        _minTemp.value = response.body()!!.main.temp_min.toInt()
        _maxTemp.value = response.body()!!.main.temp_max.toInt()
        _visibility.value = response.body()!!.visibility / 100
        _humidity.value = response.body()!!.main.humidity
        _wind.value = response.body()!!.wind.speed.toInt()
        _sunrise.value = calculateLocalTime(response, response.body()!!.sys.sunrise, true)
        _sunset.value = calculateLocalTime(response, response.body()!!.sys.sunset, true)
        _lastUpdateTime.value = calculateLocalTime(response, response.body()!!.dt, false)
        updateSharedPreferences() //Coroutine
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
            sharedPrefImpl.setValue(citySP, city.value)
            sharedPrefImpl.setValue(currentTempSP, currentTemp.value.toString())
            sharedPrefImpl.setValue(feelsLikeTempSP, feelsLikeTemp.value.toString())
            sharedPrefImpl.setValue(minTempSP, minTemp.value.toString())
            sharedPrefImpl.setValue(maxTempSP, maxTemp.value.toString())
            sharedPrefImpl.setValue(visibilitySP, visibility.value.toString())
            sharedPrefImpl.setValue(humiditySP, humidity.value.toString())
            sharedPrefImpl.setValue(windSP, wind.value.toString())
            sharedPrefImpl.setValue(sunriseSP, sunrise.value)
            sharedPrefImpl.setValue(sunsetSP, sunset.value)
            sharedPrefImpl.setValue(lastUpdateTimeSP, lastUpdateTime.value)
        }
        _hasSharedPref.value = true
    }

    fun searchActivated() {
        _isSearchActive.value = _isSearchActive.value != true
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
        _city.value = cityFromMain!!
        _currentTemp.value = currentTempFromMain!!.toInt()
        _feelsLikeTemp.value = feelsLikeTempFromMain!!.toInt()
        _minTemp.value = minTempFromMain!!.toInt()
        _maxTemp.value = maxTempFromMain!!.toInt()
        _visibility.value = visibilityFromMain!!.toInt()
        _humidity.value = humidityFromMain!!.toInt()
        _wind.value = windFromMain!!.toInt()
        _sunrise.value = sunriseFromMain!!
        _sunset.value = sunsetFromMain!!
        _lastUpdateTime.value = lastUpdateTimeFromMain!!
        _hasSharedPref.value = true
    }

    companion object {
        private const val API_KEY = "7d9c2f60d1047b2aaae0639fdd393995"
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