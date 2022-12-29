package com.example.umbrella.ui.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.umbrella.R
import com.example.umbrella.ui.main.MainUiState

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
                .align(Alignment.CenterVertically)
        ) {
            SunInfoColumn(modifier, mainUiState.sunrise, R.string.dawn, R.drawable.ic_sunrise)
        }
        Spacer(modifier.size(8.dp))
        Column(
            modifier = modifier
                .weight(.5f)
                .align(Alignment.CenterVertically)
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
                .align(Alignment.Center)
        )
    }
}