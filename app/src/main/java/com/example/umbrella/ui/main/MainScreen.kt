package com.example.umbrella.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.umbrella.R
import com.example.umbrella.ui.common.ProcessField
import com.example.umbrella.ui.common.SnackBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val mainUiState by viewModel.mainUiState.collectAsState()
    val modifier: Modifier = Modifier
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    // TO FORCE Show search bar if user landed for the first time!
    if (!mainUiState.hasSharedPref && !mainUiState.isSearchActive) {
        viewModel.searchActivated()
    }

    if (!mainUiState.hasConnection) {
        SnackBar(
            stringResource(id = R.string.no_connection),
            stringResource(id = R.string.dismiss),
            scaffoldState
        )
    }

    Scaffold(scaffoldState = scaffoldState) {
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
                    shape = RoundedCornerShape(16.dp),
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.searchActivated()
                            viewModel.checkConnection()
                        },
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    if (mainUiState.isInProcess) ProcessField(modifier) else WeatherInfo(
                        modifier,
                        mainUiState
                    )
                }
                Spacer(modifier.size(16.dp))
                Card(
                    modifier = modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    if (mainUiState.isInProcess) ProcessField(modifier) else AirConditionInfo(
                        modifier,
                        mainUiState
                    )
                }
                Spacer(modifier.size(16.dp))
                Card(
                    modifier = modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    if (mainUiState.isInProcess) ProcessField(modifier) else SunInfo(
                        modifier,
                        mainUiState
                    )
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
                    viewModel.callApiForResult(cityForSearch, null, null)
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