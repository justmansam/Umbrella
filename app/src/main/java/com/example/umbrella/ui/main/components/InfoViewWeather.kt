package com.example.umbrella.ui.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.umbrella.R
import com.example.umbrella.ui.common.mapToDrawableResource
import com.example.umbrella.ui.main.MainUiState

@Composable
fun WeatherInfo(modifier: Modifier, mainUiState: MainUiState) {
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
                    .align(Alignment.CenterVertically),
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