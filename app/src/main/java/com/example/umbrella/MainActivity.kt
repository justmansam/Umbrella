package com.example.umbrella

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.umbrella.data.local.pref.SharedPreferencesImpl
import com.example.umbrella.ui.main.MainScreen
import com.example.umbrella.ui.main.MainViewModel
import com.example.umbrella.ui.theme.UmbrellaTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

lateinit var sharedPref: SharedPreferences
lateinit var sharedPrefImpl: SharedPreferencesImpl
lateinit var fusedLocationClient: FusedLocationProviderClient
lateinit var permissionToAsk: ActivityResultLauncher<Array<String>>

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getPreferences(Context.MODE_PRIVATE)

        setScreenContent()
        viewModel.lookForSharedPref()
        checkLocationPermission()
        lifecycleScope.launchWhenCreated {
            checkConnection()
        }
    }

    private fun setScreenContent() {
        setContent {
            UmbrellaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    private fun checkLocationPermission() {
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
                        viewModel.getLocation()
                    }
                    else -> {
                        // No location access granted.
                        Toast.makeText(this, R.string.permission_reminder, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            viewModel.getLocation()
        }
    }

    private fun checkConnection() {
        val connectivityManager =
            ContextCompat.getSystemService(this, ConnectivityManager::class.java)
        val currentNetwork = connectivityManager?.activeNetwork
        if (currentNetwork == null) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show()
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
}