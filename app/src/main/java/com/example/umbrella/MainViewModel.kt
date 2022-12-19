package com.example.umbrella

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.api.RetrofitInstance
import com.example.umbrella.common.toCelsius
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
    private val _currentTemp: MutableStateFlow<Int> = MutableStateFlow(100)
    val currentTemp = _currentTemp.asStateFlow() //STATE FLOW
    private val _feelsLikeTemp: MutableStateFlow<Int> = MutableStateFlow(100)
    val feelsLikeTemp = _feelsLikeTemp.asStateFlow() //STATE FLOW
    private val _minTemp: MutableStateFlow<Int> = MutableStateFlow(100)
    val minTemp = _minTemp.asStateFlow() //STATE FLOW
    private val _maxTemp: MutableStateFlow<Int> = MutableStateFlow(100)
    val maxTemp = _maxTemp.asStateFlow() //STATE FLOW

    fun showApiCallResult(city: String?, latitude: String?, longitude: String?) {
        //var temp: Double? = null
        viewModelScope.launch {
            val response = try {
                if (latitude != null && longitude != null) {
                    RetrofitInstance.api.getWeatherByCoordination(
                        latitude,
                        longitude,
                        "7d9c2f60d1047b2aaae0639fdd393995"
                    )
                } else {
                    RetrofitInstance.api.getWeatherByCity(
                        city!!,
                        "7d9c2f60d1047b2aaae0639fdd393995"
                    )
                }
            } catch (e: IOException) {
                // CHECK INTERNET
                Log.e("TAGGG ", "IOExeption, you might not have an internet")
                return@launch
            } catch (e: HttpException) {
                Log.e("TAGGG ", "HttpException, unexpected response")
                return@launch
            }
            if (response.isSuccessful) {
                Log.i("RESPONSE ", response.body()!!.main.temp.toString())
                val tempKelvin = response.body()!!.main.temp
                _currentTemp.value = tempKelvin.toCelsius().toInt()
                val feelsLikeTempKelvin = response.body()!!.main.feels_like
                _feelsLikeTemp.value = feelsLikeTempKelvin.toCelsius().toInt()
                val minTempKelvin = response.body()!!.main.temp_min
                _minTemp.value = minTempKelvin.toCelsius().toInt()
                val maxTempKelvin = response.body()!!.main.temp_max
                _maxTemp.value = maxTempKelvin.toCelsius().toInt()
                Log.i("RESPONSE2 ", currentTemp.value.toString())
            } else {
                Log.e("TAGGG ", "Check the city name you typed")
            }
        }
    }
}