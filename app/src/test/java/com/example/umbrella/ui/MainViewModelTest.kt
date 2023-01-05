package com.example.umbrella.ui

import com.example.umbrella.ui.main.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
class MainViewModelTest {
    private val viewModel = MainViewModel()
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun mainViewModel_ResponseIsSuccessful_UiStateUpdated() = runBlocking {
        viewModel.callApiForResult(null, "59.3326", "18.0649")

        delay(3000) // To let ui state updated

        val currentUiDataState = viewModel.uiDataState.value
        // Assert that showApiCallResult() method updates city correctly.
        assertEquals("Stockholm", currentUiDataState.city)
    }
}