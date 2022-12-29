package com.example.umbrella.ui.common

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SnackBar(
    message: String,
    action: String,
    scaffoldState: ScaffoldState
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Scaffold {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    SnackbarDuration.Indefinite
                )
            }
        }
    }
}