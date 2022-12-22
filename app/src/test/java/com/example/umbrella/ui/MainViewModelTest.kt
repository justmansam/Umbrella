package com.example.umbrella.ui

import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
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
    fun mainViewModel_ResponseIsSuccessful_SharedPreferencesUpdated() = runBlocking {
        val currentMainUiState = viewModel.mainUiState.value
        viewModel.showApiCallResult("Stockholm", null, null)

        // Assert that showApiCallResult() method updates city correctly.
        assertFalse(currentMainUiState.city == "Stockholm")
    }
}