package com.github.ryjen.jokeapp.test.ui.jokes.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.ryjen.jokeapp.domain.model.Joke
import com.github.ryjen.jokeapp.ui.jokes.favourites.FavoritesAction
import com.github.ryjen.jokeapp.ui.jokes.favourites.FavoritesContent
import com.github.ryjen.jokeapp.ui.jokes.favourites.FavoritesViewState
import com.github.ryjen.jokeapp.ui.theme.MainTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Exercise visible list and confirmed removal against a controlled view state. */
class FavoritesScreenTest {
    @get:Rule val composeTestRule = createComposeRule()
    private val joke = Joke("joke-1", "A favorite joke")
    private var state by mutableStateOf(FavoritesViewState(jokes = listOf(joke)))

    @Before fun setUp() {
        composeTestRule.setContent {
            MainTheme {
                FavoritesContent(state) { action ->
                    if (action is FavoritesAction.Remove) {
                        state = state.copy(jokes = state.jokes.filterNot { it.id == action.data.id })
                    }
                }
            }
        }
    }

    @Test fun testEmpty() {
        composeTestRule.runOnUiThread { state = FavoritesViewState() }
        composeTestRule.onNodeWithTag("remove").assertDoesNotExist()
    }

    @Test fun testRemoveJoke() {
        composeTestRule.onNodeWithTag("remove").performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithText(joke.content).assertDoesNotExist()
    }
}
