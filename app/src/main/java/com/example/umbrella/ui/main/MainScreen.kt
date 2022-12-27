package com.example.umbrella.ui.main

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
import androidx.compose.ui.Alignment.Companion.Center
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
import com.example.umbrella.ui.common.mapToDrawableResource

@Composable
fun MainScreen(
    screenContentArray: Array<String>?,
    viewModel: MainViewModel = hiltViewModel()
) {
    val mainUiState by viewModel.mainUiState.collectAsState()
    val modifier: Modifier = Modifier

    // TO FORCE Show search bar if user landed for the first time or still didn't give location permission!
    if (!mainUiState.apiHasResponse && !mainUiState.hasSharedPref && !mainUiState.hasLocation && !mainUiState.isSearchActive && mainUiState.isSearchFailed == 0) {
        viewModel.lookForSharedPref() //Check it!!!???
        viewModel.searchActivated()
    }

    // TO Avoid unnecessary recomposition which removes search error message!
    if (!mainUiState.apiHasResponse && screenContentArray != null && mainUiState.isSearchFailed == 0) {
        /*
         * TO Show weather accordingly if user gave location permission
         * (latitude and longitude) on app start (for once in case of unnecessary recomposition)!
         */
        viewModel.showApiCallResult(null, screenContentArray[0], screenContentArray[1])
    }

    // MAIN SCREEN
    Column {
        if (mainUiState.isSearchActive) {
            SearchField(modifier, viewModel, mainUiState)
        }
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier.size(16.dp))
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { viewModel.searchActivated() },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                if (mainUiState.isInProcess) ProcessField(modifier) else ForecastField(modifier, mainUiState)
            }
            Spacer(modifier.size(16.dp))
            Card(
                modifier = modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                if (mainUiState.isInProcess) ProcessField(modifier) else SecondaryInfo(modifier, mainUiState)
            }
            Spacer(modifier.size(16.dp))
            Card(
                modifier = modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface
            ) {
                if (mainUiState.isInProcess) ProcessField(modifier) else SunInfo(modifier, mainUiState)
            }
            Spacer(modifier.size(16.dp))
            if (!mainUiState.isInProcess) {
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.last_update)
                            + " " + mainUiState.lastUpdateTime
                            + " " + stringResource(id = R.string.local_time),
                    fontSize = 10.sp
                )
            }
            Spacer(modifier.size(16.dp))
        }
    }
}

@Composable
fun ProcessField(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(200.dp)
    ) {
        CircularProgressIndicator(
            modifier = modifier
                .size(64.dp)
                .align(Center)
        )
    }
}

@Composable
fun SearchField(modifier: Modifier, viewModel: MainViewModel, mainUiState: MainUiState) {
    var cityForSearch by remember { mutableStateOf("") }
    Column {
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
                onSearch = {
                    viewModel.showApiCallResult(cityForSearch, null, null)
                    cityForSearch = ""
                }
            ),
            maxLines = 1
        )
        when (mainUiState.isSearchFailed) {
            1 -> Text(
                modifier = modifier.padding(16.dp),
                text = stringResource(id = R.string.search_error_typo)
            )
            2 -> Text(
                modifier = modifier.padding(16.dp),
                text = stringResource(id = R.string.search_error_connection)
            )
            3 -> Text(
                modifier = modifier.padding(16.dp),
                text = stringResource(id = R.string.search_error_unexpected)
            )
        }
    }
}

@Composable
fun ForecastField(modifier: Modifier, mainUiState: MainUiState) {
    Column(
        modifier = modifier
            .padding(
                top = 16.dp,
                bottom = 24.dp,
                start = 24.dp,
                end = 16.dp
            )
            .fillMaxSize()
    ) {
        Row(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .weight(.65f)
                    .align(CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mainUiState.currentTemperature.toString() + "\u00B0",
                    fontSize = 84.sp,
                    modifier = modifier.fillMaxWidth()
                )
                Spacer(modifier.size(4.dp))
                Text(
                    text = mainUiState.city,
                    fontSize = 32.sp,
                    modifier = modifier.fillMaxWidth()
                )
                Spacer(modifier.size(8.dp))
                Text(
                    text = stringResource(id = R.string.feels_like)
                            + " "
                            + mainUiState.feelsLikeTemperature.toString()
                            + "\u00B0",
                    fontSize = 16.sp,
                    modifier = modifier.fillMaxWidth()
                )
                Spacer(modifier.size(3.dp))
                Text(
                    text = "Min "
                            + mainUiState.minTemperature.toString()
                            + "\u00B0 / Max "
                            + mainUiState.maxTemperature.toString()
                            + "°",
                    fontSize = 16.sp,
                    modifier = modifier.fillMaxWidth()
                )
            }
            Spacer(modifier.size(8.dp))
            Image(
                modifier = modifier
                    .weight(.35f)
                    .fillMaxSize()
                    .padding(top = 16.dp),
                painter = painterResource((mainUiState.weatherIcon).mapToDrawableResource()),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun SecondaryInfo(modifier: Modifier, mainUiState: MainUiState) {
    Row(
        modifier = modifier
            .padding(vertical = 20.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Box(modifier = modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_visibility),
                    contentDescription = "",
                    modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                        .align(Center)
                )
            }
            Text(
                modifier = modifier.fillMaxSize(),
                text = stringResource(id = R.string.visibility),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = modifier.fillMaxSize(),
                text = mainUiState.visibility.toString() + " %",
                textAlign = TextAlign.Center
            )
        }
        Divider(
            modifier
                .size(2.dp, 96.dp)
                .align(CenterVertically)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Box(modifier = modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_humidity),
                    contentDescription = "",
                    modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                        .align(Center)
                )
            }
            Text(
                modifier = modifier.fillMaxSize(),
                text = stringResource(id = R.string.humidity),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = modifier.fillMaxSize(),
                text = mainUiState.humidity.toString() + " %",
                textAlign = TextAlign.Center
            )
        }
        Divider(
            modifier
                .size(2.dp, 96.dp)
                .align(CenterVertically)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Box(modifier = modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_wind),
                    contentDescription = "",
                    modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                        .align(Center)
                )
            }
            Text(
                modifier = modifier.fillMaxSize(),
                text = stringResource(id = R.string.wind),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = modifier.fillMaxSize(),
                text = mainUiState.wind.toString() + " km/h",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SunInfo(modifier: Modifier, mainUiState: MainUiState) {
    Row(
        modifier = modifier
            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .weight(.5f)
                .align(CenterVertically)
        ) {
            SunInfoColumn(modifier, mainUiState.sunrise, R.string.dawn, R.drawable.ic_sunrise)
        }
        Spacer(modifier.size(8.dp))
        Column(
            modifier = modifier
                .weight(.5f)
                .align(CenterVertically)
        ) {
            SunInfoColumn(modifier, mainUiState.sunset, R.string.dusk, R.drawable.ic_sunset)
        }
    }
}

@Composable
fun SunInfoColumn(
    modifier: Modifier,
    uiSunState: String,
    sunStringResource: Int,
    sunPainterResource: Int
) {
    Text(
        text = stringResource(id = sunStringResource),
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Text(
        text = uiSunState,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = sunPainterResource),
            contentDescription = "",
            modifier
                .size(86.dp)
                .align(Center)
        )
    }
}