package com.example.umbrella

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    //viewModel.showApiCallResult()
    Column {
        Text(text = "Selam Kankito")
        Divider(Modifier.size(16.dp))
        Text(text = viewModel.showApiCallResult().toString())
    }
}