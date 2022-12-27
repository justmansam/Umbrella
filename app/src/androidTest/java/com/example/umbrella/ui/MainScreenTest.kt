package com.example.umbrella.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.umbrella.MainActivity
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    /*
    To test according to the Phone Language:
        Turkish -> "Şehir arayın (Stockholm veya London,US)"
        English -> "Search city (Stockholm or London,US)"
        Swedish -> "Sök stad (Stockholm eller London,US)"
     */
    private val textFieldHint = "Şehir arayın (Stockholm veya London,US)"

    @get:Rule
    val activityTestRule = createAndroidComposeRule<MainActivity>()
    //val composeTestRule = createComposeRule() //PREVIOUS TEST

    @Test
    fun openAppWithoutPermission_showSearchBar() {
        activityTestRule.onNodeWithText(textFieldHint).assertExists()
        /* PREVIOUS TEST
        //composeTestRule.setContent { MainScreen(null) }
        //composeTestRule.onNodeWithText(textFieldHint).assertExists()
         */
    }
}