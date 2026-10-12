package com.github.ryjen.jokeapp.test.ui.jokes

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import com.github.ryjen.jokeapp.domain.model.Joke
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeAction
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeHeaderContent
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeViewState
import com.github.ryjen.jokeapp.ui.theme.MainTheme
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** The header, not RandomJokeScreen, owns favorite/share controls. */
class RandomJokeScreenTest {
    @get:Rule val composeTestRule = createComposeRule()
    private val joke = Joke("joke-1", "An instrumentation joke")
    private val actions = mutableListOf<RandomJokeAction>()

    @Before fun setUp() {
        Intents.init()
        composeTestRule.setContent {
            MainTheme {
                RandomJokeHeaderContent(RandomJokeViewState(joke = joke)) { actions.add(it) }
            }
        }
    }

    @After fun tearDown() {
        Intents.release()
    }

    @Test fun testShareJoke() {
        composeTestRule.onNodeWithTag("share").performClick()
        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test fun testFavourites() {
        composeTestRule.onNodeWithTag("add").performClick()
        assertThat(actions).contains(RandomJokeAction.Favorite(joke))
    }
}
