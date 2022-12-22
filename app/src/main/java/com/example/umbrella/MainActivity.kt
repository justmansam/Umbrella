package com.example.umbrella

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.umbrella.pref.SharedPreferencesImpl
import com.example.umbrella.ui.MainScreen
import com.example.umbrella.ui.MainViewModel.Companion.citySP
import com.example.umbrella.ui.MainViewModel.Companion.currentTempSP
import com.example.umbrella.ui.MainViewModel.Companion.feelsLikeTempSP
import com.example.umbrella.ui.MainViewModel.Companion.humiditySP
import com.example.umbrella.ui.MainViewModel.Companion.lastUpdateTimeSP
import com.example.umbrella.ui.MainViewModel.Companion.maxTempSP
import com.example.umbrella.ui.MainViewModel.Companion.minTempSP
import com.example.umbrella.ui.MainViewModel.Companion.sunriseSP
import com.example.umbrella.ui.MainViewModel.Companion.sunsetSP
import com.example.umbrella.ui.MainViewModel.Companion.visibilitySP
import com.example.umbrella.ui.MainViewModel.Companion.windSP
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/*
HERŞEYDEN ÖNCE İNTERNETİ KONTROL ET!!!
INTERNET YOKSA ÖNCE SHARED PREF AL!!!
SHARED VAR DİYİP STATE OLUŞTUR BÖYLECE APİ SUCCESS OLMAZSA SHARED STATE CHECK EDİLİP GÖSTERİLİR!
 */

private lateinit var fusedLocationClient: FusedLocationProviderClient
lateinit var sharedPref: SharedPreferences
lateinit var sharedPrefImpl: SharedPreferencesImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        sharedPrefImpl = SharedPreferencesImpl(sharedPref)

        lookForSharedPref()
        lifecycleScope.launchWhenCreated {
            checkConnection()
        }
        checkPermissionAndGetLocation()
    }

    private fun checkConnection() {
        val connectivityManager =
            ContextCompat.getSystemService(this, ConnectivityManager::class.java)
        val currentNetwork = connectivityManager?.activeNetwork
        if (currentNetwork == null) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndGetLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { // If permission is not granted
            Toast.makeText(this, "Belli ki izin vermemişsin!", Toast.LENGTH_SHORT).show()
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                        Toast.makeText(this, "Şimdi izin verdin sanki!", Toast.LENGTH_SHORT).show()

                        //LAST LOCATION
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    setScreenContent(
                                        latitude = location.latitude.toString(),
                                        longitude = location.longitude.toString()
                                    )
                                }
                                Toast.makeText(
                                    this,
                                    "Last location alındı!: $location",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                //lookForSharedPref()
                                Toast.makeText(
                                    this,
                                    "Last location alınamadı!: $it",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    else -> {
                        // No location access granted.
                        //lookForSharedPref()
                        Toast.makeText(this, "Oooo vermedin demek!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            //LAST LOCATION
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        setScreenContent(
                            latitude = location.latitude.toString(),
                            longitude = location.longitude.toString()
                        )
                    }
                    Toast.makeText(this, "Last location alındı!: $location", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun lookForSharedPref() {
        val storedCity = sharedPrefImpl.getValue(citySP)
        val storedCurrentTemp = sharedPrefImpl.getValue(currentTempSP)
        val storedFeelsLikeTemp = sharedPrefImpl.getValue(feelsLikeTempSP)
        val storedMinTemp = sharedPrefImpl.getValue(minTempSP)
        val storedMaxTemp = sharedPrefImpl.getValue(maxTempSP)
        val storedVisibility = sharedPrefImpl.getValue(visibilitySP)
        val storedHumidity = sharedPrefImpl.getValue(humiditySP)
        val storedWind = sharedPrefImpl.getValue(windSP)
        val storedSunrise = sharedPrefImpl.getValue(sunriseSP)
        val storedSunset = sharedPrefImpl.getValue(sunsetSP)
        val storedLastUpdateTime = sharedPrefImpl.getValue(lastUpdateTimeSP)

        if (storedCity != null && storedCurrentTemp != null && storedLastUpdateTime != null) {
            setScreenContent(
                storedCity,
                null,
                null,
                storedCurrentTemp,
                storedFeelsLikeTemp,
                storedMinTemp,
                storedMaxTemp,
                storedVisibility,
                storedHumidity,
                storedWind,
                storedSunrise,
                storedSunset,
                storedLastUpdateTime
            )
        } else {
            setScreenContent()
        }
    }

    private fun setScreenContent(
        city: String? = null,
        latitude: String? = null,
        longitude: String? = null,
        currentTemp: String? = null,
        feelsLikeTemp: String? = null,
        minTemp: String? = null,
        maxTemp: String? = null,
        visibility: String? = null,
        humidity: String? = null,
        wind: String? = null,
        sunrise: String? = null,
        sunset: String? = null,
        lastUpdateTime: String? = null
    ) {
        setContent {
            UmbrellaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(
                        city,
                        latitude,
                        longitude,
                        currentTemp,
                        feelsLikeTemp,
                        minTemp,
                        maxTemp,
                        visibility,
                        humidity,
                        wind,
                        sunrise,
                        sunset,
                        lastUpdateTime
                    )
                }
            }
        }
    }
}