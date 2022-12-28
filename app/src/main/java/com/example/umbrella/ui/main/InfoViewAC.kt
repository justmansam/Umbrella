package com.example.umbrella.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.umbrella.R

@Composable
fun AirConditionInfo(modifier: Modifier, mainUiState: MainUiState) {
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
            ACInfoColumn(
                modifier,
                mainUiState.visibility.toString(),
                R.string.visibility,
                R.drawable.ic_visibility,
                " %"
            )
        }
        Divider(
            modifier
                .size(2.dp, 96.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            ACInfoColumn(
                modifier,
                mainUiState.humidity.toString(),
                R.string.humidity,
                R.drawable.ic_humidity,
                " %"
            )
        }
        Divider(
            modifier
                .size(2.dp, 96.dp)
                .align(Alignment.CenterVertically)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            ACInfoColumn(
                modifier,
                mainUiState.wind.toString(),
                R.string.wind,
                R.drawable.ic_wind,
                " km/h"
            )
        }
    }
}

@Composable
fun ACInfoColumn(
    modifier: Modifier,
    acUiState: String,
    acStringResource: Int,
    acPainterResource: Int,
    acUnit: String
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = acPainterResource),
            contentDescription = "",
            modifier
                .size(48.dp)
                .padding(bottom = 16.dp)
                .align(Alignment.Center)
        )
    }
    Text(
        modifier = modifier.fillMaxSize(),
        text = stringResource(id = acStringResource),
        textAlign = TextAlign.Center
    )
    Text(
        modifier = modifier.fillMaxSize(),
        text = acUiState + acUnit,
        textAlign = TextAlign.Center
    )
}