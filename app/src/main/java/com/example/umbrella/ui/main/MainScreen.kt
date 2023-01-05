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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.umbrella.R
import com.example.umbrella.ui.common.ProcessField
import com.example.umbrella.ui.common.SnackBar
import com.example.umbrella.ui.main.components.AirConditionInfo
import com.example.umbrella.ui.main.components.SunInfo
import com.example.umbrella.ui.main.components.WeatherInfo
import com.example.umbrella.ui.main.model.MainUiState
import com.example.umbrella.ui.theme.Indigo900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val mainUiState by viewModel.mainUiState.collectAsState()
    val uiDataState by viewModel.uiDataState.collectAsState()
    val modifier: Modifier = Modifier
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val refreshScope = rememberCoroutineScope()
    fun refresh() = refreshScope.launch {
        viewModel.refreshActivated()
        delay(1500)
        viewModel.callApiForResult(uiDataState.city, null, null)
        viewModel.refreshActivated()
    }

    val pullRefreshState = rememberPullRefreshState(mainUiState.isRefreshing, ::refresh)

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
        Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                if (mainUiState.isSearchActive) {
                    SearchField(modifier, viewModel, mainUiState)
                }
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
                        uiDataState
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
                        uiDataState
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
                        uiDataState
                    )
                }
                Spacer(modifier.size(16.dp))
                if (!mainUiState.isInProcess) {
                    Text(
                        modifier = modifier
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.last_update)
                                + " " + uiDataState.lastUpdateTime
                                + " " + stringResource(id = R.string.local_time),
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier.size(16.dp))
            }
            PullRefreshIndicator(
                mainUiState.isRefreshing,
                pullRefreshState,
                Modifier.align(TopCenter),
                backgroundColor = Color.White,
                contentColor = Indigo900
            )
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