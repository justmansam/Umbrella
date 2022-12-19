package com.example.umbrella

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private lateinit var fusedLocationClient: FusedLocationProviderClient

/*
HERŞEYDEN ÖNCE İNTERNETİ KONTROL ET!!!
 */

open class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ÖNCE SHARED PREF VARSA ONU GÖSTER MİLLETİ BEKLETME
        checkPermissionAndGetLocation()
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
                    }
                    else -> {
                        // No location access granted.
                        Toast.makeText(this, "Oooo vermedin demek!", Toast.LENGTH_SHORT).show()
                        setScreenContent(
                            "Ankara", "null", "null"
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