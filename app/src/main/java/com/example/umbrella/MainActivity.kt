package com.example.umbrella

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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
import com.example.umbrella.ui.MainViewModel.Companion.weatherIconSP
import com.example.umbrella.ui.MainViewModel.Companion.windSP
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private lateinit var fusedLocationClient: FusedLocationProviderClient
lateinit var sharedPref: SharedPreferences
lateinit var sharedPrefImpl: SharedPreferencesImpl
lateinit var permissionToAsk: ActivityResultLauncher<Array<String>>

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
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermissionAndGetLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionAlert(this)
            permissionToAsk = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    setScreenContent(
                                        arrayOf(
                                            location.latitude.toString(),
                                            location.longitude.toString()
                                        )
                                    )
                                }
                            }
                    }
                    else -> {
                        // No location access granted.
                        Toast.makeText(this, R.string.permission_reminder, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        setScreenContent(
                            arrayOf(location.latitude.toString(), location.longitude.toString())
                        )
                    }
                }
        }
    }

    private fun showPermissionAlert(mainActivity: MainActivity) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.OK) { dialog, id ->
                    permissionToAsk.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
                }
                setNegativeButton(R.string.cancel) { dialog, id ->
                    Toast.makeText(mainActivity, R.string.permission_reminder, Toast.LENGTH_LONG)
                        .show()
                }
                setTitle(R.string.alert_title)
                setCancelable(false)
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun lookForSharedPref() {
        val sharedPrefArray = arrayOf(
            sharedPrefImpl.getValue(citySP),
            sharedPrefImpl.getValue(currentTempSP),
            sharedPrefImpl.getValue(feelsLikeTempSP),
            sharedPrefImpl.getValue(minTempSP),
            sharedPrefImpl.getValue(maxTempSP),
            sharedPrefImpl.getValue(visibilitySP),
            sharedPrefImpl.getValue(humiditySP),
            sharedPrefImpl.getValue(windSP),
            sharedPrefImpl.getValue(sunriseSP),
            sharedPrefImpl.getValue(sunsetSP),
            sharedPrefImpl.getValue(lastUpdateTimeSP),
            sharedPrefImpl.getValue(weatherIconSP)
        )
        setScreenContent(sharedPrefArray)
    }

    private fun setScreenContent(screenContentArray: Array<String?>) {
        setContent {
            UmbrellaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(
                        screenContentArray
                    )
                }
            }
        }
    }
}