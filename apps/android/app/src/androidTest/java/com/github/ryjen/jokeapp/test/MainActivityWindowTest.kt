package com.github.ryjen.jokeapp.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.ryjen.jokeapp.MainActivity
import com.github.ryjen.jokeapp.ui.R
import org.junit.Rule
import org.junit.Test
import java.util.Locale

/**
 * Launches the real activity and exercises the visible navigation surface.
 * Existing MainActivityTest only composes MainScreen inside a test-only host.
 */
class MainActivityWindowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun favoritesTabRemainsVisibleAndNavigableInRealWindow() {
        val favorites = composeTestRule.activity.getString(R.string.action_favorites)
        val favoritesTab = favorites.uppercase(Locale.getDefault())

        composeTestRule.onNodeWithText(favoritesTab).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(favorites).assertIsDisplayed()
    }
}
