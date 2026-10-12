package com.github.ryjen.jokeapp.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ryjen.jokeapp.MainActivity
import com.github.ryjen.jokeapp.test.module.fakeAppModules
import com.github.ryjen.jokeapp.ui.MainScreen
import com.github.ryjen.jokeapp.ui.theme.MainTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import androidx.test.core.app.ApplicationProvider
import org.koin.android.ext.koin.androidContext

/**
 * Test the main activity
 */
class MainActivityTest : KoinTest {
    private val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(fakeAppModules)
    }

    private val composeTestRule = createComposeRule()

    @get:Rule
    val rule: RuleChain = RuleChain.outerRule(koinTestRule).around(composeTestRule)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.github.ryjen.jokeapp", appContext.packageName)
    }

    @Test
    fun testOpensFavouriteList() {

        composeTestRule.setContent {
            MainTheme {
                MainScreen()
            }
        }
        composeTestRule.onNodeWithTag("favorites").performClick()

        composeTestRule.onNodeWithTag("favoritesList").assertIsDisplayed()
    }
}
