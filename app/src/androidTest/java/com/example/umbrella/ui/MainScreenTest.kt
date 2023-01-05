package com.example.umbrella.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.umbrella.MainActivity
import com.example.umbrella.R
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val activityTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun openAppFirstTime_showSearchBar() {
        val textFieldHint = activityTestRule.activity.getString(R.string.type_city)
        activityTestRule.onNodeWithText(textFieldHint).assertExists()
    }
}