package com.example.umbrella.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.umbrella.R

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

    // TO Show weather accordingly if user has shared preference available on app start (for once)!!!
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

    // TO Show weather accordingly if user gave location permission on app start (for once)!!!
    if (!apiHasResponse && latitudeFromMain != null && longitudeFromMain != null) {
        viewModel.showApiCallResult(null, latitudeFromMain, longitudeFromMain)
    }

    // TO Show loading state if user initiates an action to change location in the screen!!!
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
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = cityForSearch,
                    onValueChange = { cityForSearch = it },
                    label = { Text(stringResource(id = R.string.type_city)) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { viewModel.showApiCallResult(cityForSearch, null, null) }
                    ),
                    maxLines = 1
                )
                Spacer(Modifier.size(16.dp))
            }
            Card(
                modifier = Modifier
                    .weight(.48f)
                    .fillMaxSize()
                    .clickable { viewModel.searchActivated() },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(.6f)
                                .align(CenterVertically),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$currentTemperature\u00B0",
                                fontSize = 84.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = city,
                                fontSize = 32.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.size(4.dp))
                            Text(
                                text = stringResource(id = R.string.feels_like) + " $feelsLikeTemperature\u00B0",
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.size(2.dp))
                            Text(
                                text = "Min $minTemperature\u00B0 / Max $maxTemperature°",
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.size(8.dp))
                        Image(
                            modifier = Modifier
                                .weight(.4f)
                                .size(156.dp)
                                .padding(top = 8.dp),
                            painter = painterResource(id = R.drawable.defaultw),
                            contentDescription = ""
                        )
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
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = "",
                            Modifier.size(48.dp)
                        )
                        Text(text = stringResource(id = R.string.visibility))
                        Text(text = "$visibility %")
                    }
                    Spacer(Modifier.size(4.dp))
                    Divider(
                        Modifier
                            .size(2.dp, 96.dp)
                            .align(CenterVertically)
                    )
                    Spacer(Modifier.size(4.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_humidity),
                            contentDescription = "",
                            Modifier.size(48.dp)
                        )
                        Text(text = stringResource(id = R.string.humidity))
                        Text(text = "$humidity %")
                    }
                    Spacer(Modifier.size(4.dp))
                    Divider(
                        Modifier
                            .size(2.dp, 96.dp)
                            .align(CenterVertically)
                    )
                    Spacer(Modifier.size(4.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(CenterVertically)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_wind),
                            contentDescription = "",
                            Modifier.size(48.dp)
                        )
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
                        .align(CenterHorizontally)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(.5f)
                            .align(CenterVertically)
                    ) {
                        Text(
                            text = stringResource(id = R.string.dawn),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = sunrise,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_sunrise),
                            contentDescription = "",
                            Modifier.size(86.dp)
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    Column(
                        modifier = Modifier
                            .weight(.5f)
                            .fillMaxWidth()
                            .align(CenterVertically),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.dusk),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = sunset,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_sunset),
                            contentDescription = "",
                            modifier = Modifier
                                .size(86.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(Modifier.size(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(.05f),
                text = stringResource(id = R.string.last_update)
                        + " $lastUpdateTime "
                        + stringResource(id = R.string.local_time),
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