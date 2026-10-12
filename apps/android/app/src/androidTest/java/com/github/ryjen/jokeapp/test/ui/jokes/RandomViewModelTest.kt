package com.github.ryjen.jokeapp.test.ui.jokes

import com.github.ryjen.jokeapp.data.repository.joke.JokeRepository
import com.github.ryjen.jokeapp.domain.model.Joke
import com.github.ryjen.jokeapp.test.randomJoke
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeAction
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.core.parameter.parametersOf
import com.github.ryjen.jokeapp.ui.navigation.Router
import io.mockk.mockk

@ExperimentalCoroutinesApi
class RandomViewModelTest : KoinTest {

    @get:Rule
    private val viewModel: RandomJokeViewModel by inject(parameters = { parametersOf(mockk<Router>(relaxed = true)) })

    private val repo: JokeRepository by inject()

    lateinit var joke: Joke

    @Before
    fun setUp() {
        joke = randomJoke()
    }

    @Test
    fun testAddJokeToFavourites() = runTest {
        viewModel.dispatch(RandomJokeAction.Favorite(joke))
        val actual = repo.getJoke(joke.id)
        assertThat(actual).isNotNull()
    }


    @Test
    fun testRemoveJokeFromFavourites() = runTest {
        viewModel.dispatch(RandomJokeAction.UnFavorite(joke))

        val actual = repo.getJoke(joke.id)
        assertThat(actual).isNull()
    }


    @Test
    fun testRefreshJoke() {

        viewModel.dispatch(RandomJokeAction.Refresh(joke))

        // FIXME: actual test
    }
}
