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
    private val _tempr: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val tempr = _tempr.asStateFlow() //STATE FLOW

    //var temp: Double by mutableStateOf(0.0) //COMPOSE STATE as an alternative

    fun showApiCallResult(city: String) {
        //var temp: Double? = null
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.getWeatherByCity(city, "7d9c2f60d1047b2aaae0639fdd393995")
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
                _tempr.value = response.body()!!.main.temp
                _tempr.value -= 272.15 //Extension func kullanarak virgülden sonrasını kırp ve derece işareti ekle
                Log.i("RESPONSE2 ", tempr.value.toString())
            } else {
                Log.e("TAGGG ", "Response is not successful")
            }
        }
    }
}