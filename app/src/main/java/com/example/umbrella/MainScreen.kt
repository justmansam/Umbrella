package com.example.umbrella

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    cityFromMain: String?,
    latitudeFromMain: String?,
    longitudeFromMain: String?,
    currentTempFromMain: String?,
    feelsLikeTempFromMain: String?,
    minTempFromMain: String?,
    maxTempFromMain: String?,
    visibilityFromMain: String?,
    humidityFromMain: String?,
    windFromMain: String?,
    sunriseFromMain: String?,
    sunsetFromMain: String?,
    lastUpdateTimeFromMain: String?,
    viewModel: MainViewModel = hiltViewModel()
) {
    val apiHasResponse by viewModel.apiHasResponse.collectAsState()
    val hasLocation by viewModel.hasLocation.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val isSearchFailed by viewModel.isSearchFailed.collectAsState()
    val hasSharedPref by viewModel.hasSharedPref.collectAsState()
    val city by viewModel.city.collectAsState()
    //val latitude by viewModel.latitude.collectAsState()
    //val longitude by viewModel.longitude.collectAsState()
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

    if (!hasSharedPref && cityFromMain != null && currentTempFromMain != null && lastUpdateTimeFromMain != null) {
        viewModel.exposeLocalData(
            cityFromMain,
            currentTempFromMain,
            feelsLikeTempFromMain,
            minTempFromMain,
            maxTempFromMain,
            visibilityFromMain,
            humidityFromMain,
            windFromMain,
            sunriseFromMain,
            sunsetFromMain,
            lastUpdateTimeFromMain
        )
    }

    if (!apiHasResponse) {
        if (latitudeFromMain != null && longitudeFromMain != null) {
            viewModel.showApiCallResult(null, latitudeFromMain, longitudeFromMain)
        } else {
            //Send default place or remember (shared pref) the previous place that user selected
            viewModel.showApiCallResult(cityFromMain, null, null)
        }
    }

    if (!hasLocation && !hasSharedPref && !isSearchActive) {
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
            if (isSearchActive) {
                var cityForSearch by remember {
                    mutableStateOf("")
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = cityForSearch,
                        onValueChange = { cityForSearch = it },
                        label = { Text("Type city (Stockholm or London,US)") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        maxLines = 1
                    )
                    Button(onClick = {
                        //viewModel.searchActivated()
                        viewModel.showApiCallResult(cityForSearch, null, null)
                    }) {}
                }
                Spacer(Modifier.size(16.dp))
            }
            Card(
                modifier = Modifier
                    .weight(.48f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .weight(.5f)
                                .clickable {
                                    viewModel.searchActivated()
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
            }
            Spacer(Modifier.size(16.dp))
            Card(
                modifier = Modifier
                    .weight(.26f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
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
            }
            Spacer(Modifier.size(16.dp))
            Card(
                modifier = Modifier
                    .weight(.26f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
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
            }
            Spacer(Modifier.size(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(.07f),
                text = stringResource(id = R.string.last_update) + " $lastUpdateTime",
                fontSize = 10.sp
            )
        }
    }

    /*
    //Should be checked once! It shown every time it recomposes. So send a hasShown message to viewModel
    if (isSearchActive) {
        Toast.makeText(context, "Type a city name", Toast.LENGTH_SHORT).show()
    }
     */

    /*
    TAM ÇALIŞMIYOR (TOAST IS SHOWN EKLE!)
     */
    /*
    val notFoundAlert = Toast.makeText(
        context,
        "City not found! Please check the city name you entered!",
        Toast.LENGTH_LONG
    )
    if (isSearchFailed) {
        notFoundAlert.show()
    } else {
        notFoundAlert.cancel()
    }

     */
}