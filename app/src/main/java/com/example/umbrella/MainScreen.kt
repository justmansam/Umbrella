package com.example.umbrella

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    city1: String,
    latitude: String,
    longitude: String,
    viewModel: MainViewModel = hiltViewModel()
) {
    val apiSuccess by viewModel.apiSuccess.collectAsState()
    val city by viewModel.city.collectAsState()
    val currentTemperature by viewModel.currentTemp.collectAsState()
    val feelsLikeTemperature by viewModel.feelsLikeTemp.collectAsState()
    val minTemperature by viewModel.minTemp.collectAsState()
    val maxTemperature by viewModel.maxTemp.collectAsState()
    val visibility by viewModel.visibility.collectAsState()
    val humidity by viewModel.humidity.collectAsState()
    val wind by viewModel.wind.collectAsState()
    val sunrise by viewModel.sunrise.collectAsState()
    val sunset by viewModel.sunset.collectAsState()
    val lastUpdateTime by viewModel.lastUpdateTime.collectAsState()

    val context = LocalContext.current

    // First get default shared pref city value first and use it by remember
    // Then change it with new coordinations and city

    if (latitude != "null" && longitude != "null") {
        viewModel.showApiCallResult(null, latitude, longitude)
    } else {
        //Send default place or remember (shared pref) the previous place that user selected
        viewModel.showApiCallResult(city1, null, null)
    }

    if (!apiSuccess) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .weight(.48f)
                    .size(64.dp)
                    .align(CenterHorizontally)
                    .padding(top = 32.dp)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .weight(.24f)
                    .size(64.dp)
                    .align(CenterHorizontally)
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .weight(.24f)
                    .size(64.dp)
                    .align(CenterHorizontally)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .weight(.48f)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .weight(.5f)
                            .clickable {
                                changeLocation(viewModel, context)
                            }
                    ) {
                        Text(text = "$currentTemperature\u00B0", fontSize = 32.sp)
                        Text(text = city, fontSize = 32.sp)
                        Text(
                            text = stringResource(id = R.string.feels_like) + " $feelsLikeTemperature\u00B0",
                            fontSize = 16.sp
                        )
                        Row {
                            Text(text = "Min $minTemperature\u00B0 / ", fontSize = 16.sp)
                            Text(text = "Max $maxTemperature\u00B0", fontSize = 16.sp)
                        }
                    }
                    Spacer(Modifier.size(8.dp))
                    Text(
                        modifier = Modifier.weight(.5f),
                        text = "TEXT to MOCK image FOR air"
                    )
                    //Image(painter = , contentDescription = )
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .weight(.26f)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "TEXT to MOCK image FOR visibility")
                    //Image(painter = , contentDescription = )
                    Text(text = stringResource(id = R.string.visibility))
                    Text(text = "$visibility %")
                }
                Spacer(Modifier.size(4.dp))
                Divider(Modifier.size(2.dp, 96.dp))
                Spacer(Modifier.size(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "TEXT to MOCK image FOR humidity")
                    //Image(painter = , contentDescription = )
                    Text(text = stringResource(id = R.string.humidity))
                    Text(text = "$humidity %")
                }
                Spacer(Modifier.size(4.dp))
                Divider(Modifier.size(2.dp, 96.dp))
                Spacer(Modifier.size(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "TEXT to MOCK image FOR wind")
                    //Image(painter = , contentDescription = )
                    Text(text = stringResource(id = R.string.wind))
                    Text(text = "$wind km/h")
                }
            }
            Spacer(Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .weight(.26f)
            ) {
                Column(modifier = Modifier.weight(.5f)) {
                    Text(text = stringResource(id = R.string.dawn))
                    Text(text = sunrise)
                    Text(text = "TEXT to MOCK image FOR sunrise")
                    //Image(painter = , contentDescription = )
                }
                Spacer(Modifier.size(8.dp))
                Column(modifier = Modifier.weight(.5f)) {
                    Text(text = stringResource(id = R.string.dusk))
                    Text(text = sunset)
                    Text(text = "TEXT to MOCK image FOR sunset")
                    //Image(painter = , contentDescription = )
                }
            }
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .weight(.07f),
                text = stringResource(id = R.string.last_update) + " $lastUpdateTime",
                fontSize = 10.sp
            )
        }
    }
}

fun changeLocation(viewModel: MainViewModel, context: Context) {
    Toast.makeText(context, "Şehir değiştiniz. Saçmalamayın!", Toast.LENGTH_SHORT).show()

    viewModel.showApiCallResult("Ankara", null, null)
}