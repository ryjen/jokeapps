package com.github.ryjen.jokeapp.test.ui.jokes

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import com.github.ryjen.jokeapp.MainActivity
import com.github.ryjen.jokeapp.data.repository.joke.JokeRepository
import com.github.ryjen.jokeapp.test.randomJoke
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeScreen
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeViewModel
import com.github.ryjen.jokeapp.ui.theme.MainTheme
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.core.parameter.parametersOf
import com.github.ryjen.jokeapp.ui.navigation.Router
import io.mockk.mockk

class RandomJokeScreenTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repo: JokeRepository by inject()

    private val viewModel: RandomJokeViewModel by inject(parameters = { parametersOf(mockk<Router>(relaxed = true)) })

    companion object {

        @BeforeClass
        @JvmStatic
        fun startUp() {
            Intents.init()
        }
    }

    @Before
    fun setUp() {
        val joke = randomJoke()

        runBlocking {
            repo.addFavorite(joke)
        }
        composeTestRule.setContent {
            MainTheme {
                RandomJokeScreen(viewModel)
            }
        }
    }

    @Test
    fun testShareJoke() {
        composeTestRule.onNodeWithTag("share").performClick()

        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun testFavourites() {
        composeTestRule.onNodeWithTag("add").performClick()

        composeTestRule.onNodeWithText("added").assertIsDisplayed()
    }
}
