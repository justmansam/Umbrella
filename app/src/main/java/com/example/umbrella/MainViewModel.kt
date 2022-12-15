package com.example.umbrella

import android.util.Log
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

    fun showApiCallResult(): Double? {
        var temp: Double? = null
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
                Log.i("RESPONSE2 ", temp.toString())
            } else {
                Log.e("TAGGG ", "Response is not successful")
            }
        }
        Log.i("RESPONSE3 ", temp.toString())
        return temp
    }
}