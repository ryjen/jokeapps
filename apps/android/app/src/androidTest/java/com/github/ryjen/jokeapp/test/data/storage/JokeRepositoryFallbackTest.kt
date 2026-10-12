package com.github.ryjen.jokeapp.test.data.storage

import com.github.ryjen.jokeapp.data.api.JokeService
import com.github.ryjen.jokeapp.data.model.JokeResponse
import com.github.ryjen.jokeapp.data.repository.joke.JokeRepository
import com.github.ryjen.jokeapp.data.repository.joke.LocalDataSource
import com.github.ryjen.jokeapp.data.repository.joke.RemoteDataSource
import com.github.ryjen.jokeapp.data.storage.JokeDatabase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

/** Real Android SQLite cache with a controlled remote boundary. */
class JokeRepositoryFallbackTest : KoinTest {
    private val db: JokeDatabase by inject()

    @Test fun aNormalCacheMissFetchesRemote() = runBlocking {
        val key = "remote-only-" + System.nanoTime()
        val service = mockk<JokeService>()
        coEvery { service.getJoke(key) } returns JokeResponse(key, "remote joke", 200)
        val repo = JokeRepository(LocalDataSource(db), RemoteDataSource(service))

        val result = repo.getJoke(key)
        assertThat(result?.id).isEqualTo(key)
        assertThat(result?.content).isEqualTo("remote joke")
        coVerify(exactly = 1) { service.getJoke(key) }
    }

    @Test fun aCacheHitDoesNotFetchRemote() = runBlocking {
        val key = "cached-" + System.nanoTime()
        val local = LocalDataSource(db)
        local.insertJoke(com.github.ryjen.jokeapp.data.storage.Joke(0, key, "cached joke", null, false))
        val service = mockk<JokeService>()
        val repo = JokeRepository(local, RemoteDataSource(service))

        assertThat(repo.getJoke(key)?.content).isEqualTo("cached joke")
        coVerify(exactly = 0) { service.getJoke(any()) }
    }

    @Test fun aNormalNullRandomResponseUsesCachedJoke() = runBlocking {
        val key = "fallback-" + System.nanoTime()
        val local = LocalDataSource(db)
        local.insertJoke(com.github.ryjen.jokeapp.data.storage.Joke(0, key, "offline joke", null, false))
        val service = mockk<JokeService>()
        coEvery { service.getRandomJoke() } returns null
        val repo = JokeRepository(local, RemoteDataSource(service))

        assertThat(repo.getRandomJoke()).isNotNull()
        coVerify(exactly = 1) { service.getRandomJoke() }
    }

    @Test fun remoteCancellationIsNotConvertedToEmptyResults() = runBlocking {
        val key = "cancel-" + System.nanoTime()
        val service = mockk<JokeService>()
        coEvery { service.getJoke(key) } throws CancellationException("cancel test")
        val repo = JokeRepository(LocalDataSource(db), RemoteDataSource(service))

        var cancelled = false
        try {
            repo.getJoke(key)
        } catch (e: CancellationException) {
            cancelled = true
        }
        assertThat(cancelled).isTrue()
    }
}
