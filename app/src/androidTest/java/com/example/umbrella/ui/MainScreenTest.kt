package com.example.umbrella.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.umbrella.ui.main.MainScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    private val arrayToTest = arrayOf<String?>(null)

    /*
    Phone Language:
        Turkish -> "Şehir arayın (Stockholm veya London,US)"
        English -> "Search city (Stockholm or London,US)"
        Swedish -> "Sök stad (Stockholm eller London,US)"
     */
    private val textFieldHint = "Şehir arayın (Stockholm veya London,US)"

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent { MainScreen(arrayToTest) }
    }

    @Test
    fun openAppWithoutPermission_showSearchBar() {
        composeTestRule.onNodeWithText(textFieldHint).assertExists()
    }
}