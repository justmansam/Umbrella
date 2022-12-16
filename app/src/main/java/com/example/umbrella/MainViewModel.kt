package com.example.umbrella

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.api.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    /*
    private val _viewState: MutableState<Double?> = mutableStateOf(null)
    val viewState: State<Double?> = _viewState

     */
    var temp: Double by mutableStateOf(0.0)

    fun showApiCallResult(): Double {
        //var temp: Double? = null
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.getWeather()
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
                temp = response.body()!!.main.temp
                temp -= 272.15 //Extension func kullan
                Log.i("RESPONSE2 ", temp.toString())
            } else {
                Log.e("TAGGG ", "Response is not successful")
            }
        }
        Log.i("RESPONSE3 ", temp.toString())
        return temp
    }
}