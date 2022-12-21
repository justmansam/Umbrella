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
import com.example.umbrella.MainViewModel.Companion.citySP
import com.example.umbrella.pref.SharedPreferencesImpl
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
        lifecycleScope.launchWhenCreated {
            lookForSharedPref(sharedPref)
            checkConnection()
        }
        checkPermissionAndGetLocation()
    }

    private fun checkConnection() {
        val connectivityManager =
            ContextCompat.getSystemService(this, ConnectivityManager::class.java)
        val currentNetwork = connectivityManager?.activeNetwork
        if (currentNetwork == null) {
            Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(
                                    this,
                                    "Last location alındı!: $location",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setScreenContent(
                                    "null",
                                    location?.latitude.toString(),
                                    location?.longitude.toString()
                                )
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Last location alınamadı!: $it",
                                    Toast.LENGTH_SHORT
                                ).show()
                                lookForSharedPref(sharedPref)
                                setScreenContent("null", "null", "null")
                            }
                    }
                    else -> {
                        // No location access granted.
                        Toast.makeText(this, "Oooo vermedin demek!", Toast.LENGTH_SHORT).show()
                        lookForSharedPref(sharedPref)
                        setScreenContent(
                            "null", "null", "null"
                        )
                    }
                }
            }.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            //LAST LOCATION
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    Toast.makeText(this, "Last location alındı!: $location", Toast.LENGTH_SHORT)
                        .show()
                    setScreenContent(
                        "null", location?.latitude.toString(), location?.longitude.toString()
                    )
                }
        }
    }

    private fun lookForSharedPref(sharedPref: SharedPreferences) {
        sharedPrefImpl = SharedPreferencesImpl(sharedPref)
        val storedCity = sharedPrefImpl.getValue(citySP)
        Toast.makeText(this, "ŞEHRE GELLL!: $storedCity", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setScreenContent(city: String, latitude: String, longitude: String) {
        setContent {
            UmbrellaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(city, latitude, longitude)
                }
            }
        }
    }
}