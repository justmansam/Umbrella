package com.example.umbrella

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.api.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    /*
    Compose state yerine bunun kullanılmasının sebebi app termination durumuna karşı
    SaveStateHandle ile daha verimli çalışması ve compose free olması (reusable with xml)
     */
    //var tempr: Double by mutableStateOf(0.0) //COMPOSE STATE as an alternative
    private val _apiSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val apiSuccess = _apiSuccess.asStateFlow() //STATE FLOW
    private val _city: MutableStateFlow<String> = MutableStateFlow("")
    val city = _city.asStateFlow() //STATE FLOW
    private val _currentTemp: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentTemp = _currentTemp.asStateFlow() //STATE FLOW
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

    fun showApiCallResult(city: String?, latitude: String?, longitude: String?) {
        //var temp: Double? = null
        viewModelScope.launch {
            val response = try {
                if (latitude != null && longitude != null) {
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
                Log.i("RESPONSE ", response.body()!!.main.temp.toString())

                _apiSuccess.value = true
                _currentTemp.value = response.body()!!.main.temp.toInt()
                _feelsLikeTemp.value = response.body()!!.main.feels_like.toInt()
                _minTemp.value = response.body()!!.main.temp_min.toInt()
                _maxTemp.value = response.body()!!.main.temp_max.toInt()
                _visibility.value = response.body()!!.visibility / 100
                _humidity.value = response.body()!!.main.humidity
                _wind.value = response.body()!!.wind.speed.toInt()

                Log.i("RESPONSE2 ", currentTemp.value.toString())
            } else {
                _apiSuccess.value = false
                Log.e("TAGGG ", "Check the city name you typed")
            }
        }
    }

    companion object {
        private const val API_KEY = "7d9c2f60d1047b2aaae0639fdd393995"
    }
}