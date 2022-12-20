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
    // FROM GENERAL UI STATE MANAGEMENT
    private val _apiSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val apiSuccess = _apiSuccess.asStateFlow() //STATE FLOW
    private val _hasLocation: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasLocation = _hasLocation.asStateFlow() //STATE FLOW
    private val _isSearchActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()
    private val _hasSharedPref: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasSharedPref = _hasSharedPref.asStateFlow()

    // FROM SEARCH or LOCATION
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
        //var temp: Double? = null
        viewModelScope.launch {
            val response = try {
                if (latitude != null && longitude != null) {
                    _hasLocation.value = true
                    RetrofitInstance.api.getWeatherByCoordination(
                        latitude,
                        longitude,
                        API_KEY
                    )
                } else {
                    _city.value = city!!
                    RetrofitInstance.api.getWeatherByCity(
                        city,
                        API_KEY
                    )
                }
            } catch (e: IOException) {
                // CHECK INTERNET
                Log.e("TAGGG ", "IOExeption, check your connection")
                _apiSuccess.value = false
                return@launch
            } catch (e: HttpException) {
                Log.e("TAGGG ", "HttpException, unexpected response")
                _apiSuccess.value = false
                return@launch
            }
            if (response.isSuccessful) {
                _apiSuccess.value = true
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
            } else {
                // Check for typo for city name you typed!
                _apiSuccess.value = false //BURASI BOZUK
                searchingActivated()
                Log.e("TAGGG ", "Check the city name you typed")
            }
        }
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

    fun searchingActivated() {
        _isSearchActive.value = _isSearchActive.value != true
    }

    companion object {
        private const val API_KEY = "7d9c2f60d1047b2aaae0639fdd393995"
    }
}