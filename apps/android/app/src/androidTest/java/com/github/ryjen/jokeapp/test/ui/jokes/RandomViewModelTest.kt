package com.github.ryjen.jokeapp.test.ui.jokes

import com.github.ryjen.jokeapp.domain.repository.joke.JokeRepository
import com.github.ryjen.jokeapp.domain.model.Joke
import com.github.ryjen.jokeapp.test.randomJoke
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeAction
import com.github.ryjen.jokeapp.ui.jokes.random.RandomJokeViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.core.parameter.parametersOf
import com.github.ryjen.jokeapp.ui.navigation.Router
import io.mockk.mockk

@ExperimentalCoroutinesApi
class RandomViewModelTest : KoinTest {

    private val viewModel: RandomJokeViewModel by inject(parameters = { parametersOf(mockk<Router>(relaxed = true)) })

    private val repo: JokeRepository by inject()

    lateinit var joke: Joke

    @Before
    fun setUp() {
        joke = randomJoke()
    }

    @Test
    fun testAddJokeToFavourites() = runBlocking {
        viewModel.dispatch(RandomJokeAction.Refresh(joke))
        viewModel.dispatch(RandomJokeAction.Favorite(joke))
        withTimeout(3000L) {
            while (repo.getJoke(joke.id)?.isFavorite != true) delay(25L)
        }
        assertThat(repo.getJoke(joke.id)?.isFavorite).isTrue()
    }


    @Test
    fun testRemoveJokeFromFavourites() = runBlocking {
        repo.addFavorite(joke)
        viewModel.dispatch(RandomJokeAction.Refresh(joke))
        viewModel.dispatch(RandomJokeAction.UnFavorite(joke))
        withTimeout(3000L) {
            while (repo.getJoke(joke.id)?.isFavorite != false) delay(25L)
        }
        assertThat(repo.getJoke(joke.id)?.isFavorite).isFalse()
    }


    @Test
    fun testRefreshJoke() {

        viewModel.dispatch(RandomJokeAction.Refresh(joke))

        // FIXME: actual test
    }
}
