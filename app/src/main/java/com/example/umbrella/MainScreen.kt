package com.example.umbrella

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    viewModel.showApiCallResult() //Send default place or remember (shared pref) the previous place that user selected

    val curTemp by viewModel.tempr.collectAsState()

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
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(.5f)) {
                    //Text(text = "-17")
                    Text(text = "$curTemp")
                    Text(text = "Uppsala")
                }
                Spacer(Modifier.size(4.dp))
                Text(
                    modifier = Modifier.weight(.5f),
                    text = "TEXT to MOCK image FOR air"
                )
                //Image(painter = , contentDescription = )
            }
            Text(text = "-14/-17 Feels like -18")
        }
        Spacer(Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .weight(.26f)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Dusk")
                Text(text = "08:45")
                Text(text = "TEXT to MOCK image FOR sunrise")
                //Image(painter = , contentDescription = )
            }
            Spacer(Modifier.size(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Dawn")
                Text(text = "14:43")
                Text(text = "TEXT to MOCK image FOR sunset")
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
                Text(text = "Visibility")
                Text(text = "100%")
            }
            Spacer(Modifier.size(4.dp))
            Divider(Modifier.size(2.dp, 96.dp))
            Spacer(Modifier.size(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "TEXT to MOCK image FOR humidity")
                //Image(painter = , contentDescription = )
                Text(text = "Humidity")
                Text(text = "88%")
            }
            Spacer(Modifier.size(4.dp))
            Divider(Modifier.size(2.dp, 96.dp))
            Spacer(Modifier.size(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "TEXT to MOCK image FOR wind")
                //Image(painter = , contentDescription = )
                Text(text = "Wind")
                Text(text = "3 km/h")
            }
        }
    }
}