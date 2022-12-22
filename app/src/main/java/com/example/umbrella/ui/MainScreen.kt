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
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val mainUiState by viewModel.mainUiState.collectAsState()

    // TO Show weather accordingly if user has shared preference available on app start (for once)!!!
    if (!mainUiState.hasSharedPref && cityFromMain != null && currentTempFromMain != null && lastUpdateTimeFromMain != null) {
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

    // TO Show weather accordingly if user gave location permission on app start (for once in case of recomposition)!
    if (!mainUiState.apiHasResponse && latitudeFromMain != null && longitudeFromMain != null) {
        viewModel.showApiCallResult(null, latitudeFromMain, longitudeFromMain)
    }

    // MAIN SCREEN
    if (mainUiState.isInProcess) {
        ProcessField(modifier)
    } else {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (mainUiState.isSearchActive) {
                SearchField(modifier, viewModel)
            }
            Card(
                modifier = modifier
                    .weight(.50f)
                    .fillMaxSize()
                    .clickable { viewModel.searchActivated() },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                ForecastField(modifier, mainUiState)
            }
            Spacer(modifier.size(16.dp))
            Card(
                modifier = modifier
                    .weight(.28f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                SecondaryInfo(modifier, mainUiState)
            }
            Spacer(modifier.size(16.dp))
            Card(
                modifier = modifier
                    .weight(.28f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                SunInfo(modifier, mainUiState)
            }
            Spacer(modifier.size(16.dp))
            Text(
                modifier = modifier
                    .fillMaxSize()
                    .weight(.05f),
                text = stringResource(id = R.string.last_update)
                        + " " + mainUiState.lastUpdateTime
                        + " " + stringResource(id = R.string.local_time),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ProcessField(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = modifier
                .weight(.48f)
                .size(64.dp)
                .align(CenterHorizontally)
                .padding(top = 32.dp)
        )
        CircularProgressIndicator(
            modifier = modifier
                .weight(.24f)
                .size(64.dp)
                .align(CenterHorizontally)
        )
        CircularProgressIndicator(
            modifier = modifier
                .weight(.24f)
                .size(64.dp)
                .align(CenterHorizontally)
        )
    }
}

@Composable
fun SearchField(modifier: Modifier, viewModel: MainViewModel) {
    var cityForSearch by remember {
        mutableStateOf("")
    }
    TextField(
        modifier = modifier.fillMaxWidth(),
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
    Spacer(modifier.size(16.dp))
}

@Composable
fun ForecastField(modifier: Modifier, mainUiState: MainUiState) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .weight(.6f)
                    .align(CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mainUiState.currentTemperature.toString() + "\u00B0",
                    fontSize = 84.sp,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = mainUiState.city,
                    fontSize = 32.sp,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier.size(4.dp))
                Text(
                    text = stringResource(id = R.string.feels_like) + " " + mainUiState.feelsLikeTemperature.toString() + "\u00B0",
                    fontSize = 16.sp,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier.size(2.dp))
                Text(
                    text = "Min " + mainUiState.minTemperature.toString() + "\u00B0 / Max " + mainUiState.maxTemperature.toString() + "°",
                    fontSize = 16.sp,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier.size(8.dp))
            Image(
                modifier = modifier
                    .weight(.4f)
                    .size(156.dp)
                    .padding(top = 8.dp),
                painter = painterResource(id = R.drawable.defaultw),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun SecondaryInfo(modifier: Modifier, mainUiState: MainUiState) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_visibility),
                contentDescription = "",
                modifier.size(48.dp)
            )
            Text(text = stringResource(id = R.string.visibility))
            Text(text = mainUiState.visibility.toString() + " %")
        }
        Spacer(modifier.size(4.dp))
        Divider(
            modifier
                .size(2.dp, 96.dp)
                .align(CenterVertically)
        )
        Spacer(modifier.size(4.dp))
        Column(
            modifier = modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_humidity),
                contentDescription = "",
                modifier.size(48.dp)
            )
            Text(text = stringResource(id = R.string.humidity))
            Text(text = mainUiState.humidity.toString() + " %")
        }
        Spacer(modifier.size(4.dp))
        Divider(
            modifier
                .size(2.dp, 96.dp)
                .align(CenterVertically)
        )
        Spacer(modifier.size(4.dp))
        Column(
            modifier = modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_wind),
                contentDescription = "",
                modifier.size(48.dp)
            )
            Text(text = stringResource(id = R.string.wind))
            Text(text = mainUiState.wind.toString() + " km/h")
        }
    }
}

@Composable
fun SunInfo(modifier: Modifier, mainUiState: MainUiState) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .weight(.5f)
                .align(CenterVertically)
        ) {
            Text(
                text = stringResource(id = R.string.dawn),
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = mainUiState.sunrise,
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(id = R.drawable.ic_sunrise),
                contentDescription = "",
                modifier.size(86.dp)
            )
        }
        Spacer(modifier.size(8.dp))
        Column(
            modifier = modifier
                .weight(.5f)
                .fillMaxWidth()
                .align(CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.dusk),
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = mainUiState.sunset,
                modifier = modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(id = R.drawable.ic_sunset),
                contentDescription = "",
                modifier = modifier
                    .size(86.dp)
                    .fillMaxWidth()
            )
        }
    }
}