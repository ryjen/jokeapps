package com.github.ryjen.jokeapp.test.ui.jokes.favorites

import com.github.ryjen.jokeapp.domain.repository.joke.JokeRepository
import com.github.ryjen.jokeapp.test.randomJoke
import com.github.ryjen.jokeapp.ui.jokes.favourites.FavoritesAction
import com.github.ryjen.jokeapp.ui.jokes.favourites.FavoritesViewModel
import org.junit.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
class FavoritesViewModelTest : KoinTest {

    private val viewModel: FavoritesViewModel by inject()
    private val repo: JokeRepository by inject()

    @Test
    fun testRemoveJoke() = runBlocking {
        val joke = randomJoke()
        repo.addFavorite(joke)
        viewModel.dispatch(FavoritesAction.Remove(joke))
        withTimeout(3000L) {
            while (repo.getJoke(joke.id)?.isFavorite != false) delay(25L)
        }
        assertEquals(false, repo.getJoke(joke.id)?.isFavorite)
    }
}
