package com.example.umbrella.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProcessField(modifier: Modifier) {
    Box(
        modifier = modifier
            .size(200.dp)
    ) {
        CircularProgressIndicator(
            modifier = modifier
                .size(64.dp)
                .align(Alignment.Center)
        )
    }
}