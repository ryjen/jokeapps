package com.github.ryjen.jokeapp.data.repository.joke

import com.github.ryjen.jokeapp.data.mapping.JokeMapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import com.github.ryjen.jokeapp.domain.model.Joke as DomainJoke
import com.github.ryjen.jokeapp.domain.repository.joke.JokeRepository as DomainJokeRepository

class JokeRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val map: JokeMapper = JokeMapper
) : DomainJokeRepository {

    override fun getFavoriteJokes(): Flow<List<DomainJoke>> =
        localDataSource.getFavoriteJokes().map { list ->
            list.map { map(it) }
        }

    // Prefer a remote random joke, falling back to cache for offline/no-result cases.
    override suspend fun getRandomJoke(): DomainJoke? {
        val remote = try {
            remoteDataSource.getRandomJoke()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "error fetching remote random joke")
            null
        }
        return remote?.let { map(it) } ?: localDataSource.getRandomJoke()?.let { map(it) }
    }

    // A normal cache miss is not an exception: query remote when absent.
    override suspend fun getJoke(id: String): DomainJoke? {
        val cached = try {
            localDataSource.getJoke(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "error fetching local joke")
            null
        }
        return cached?.let { map(it) } ?: remoteDataSource.getJoke(id)?.let { map(it) }
    }

    // Atomic insert-or-favorite, safe for repeated writes of the same external key.
    override suspend fun addFavorite(joke: DomainJoke) {
        localDataSource.addFavorite(map(joke.copy(isFavorite = true)))
    }

    // Explicit Unit return isolates SQLDelight query-result changes from the domain contract.
    override suspend fun removeFavorite(joke: DomainJoke) {
        localDataSource.updateFavorite(joke.id, false)
    }

    // save a local cache of a joke
    override suspend fun cacheJoke(joke: DomainJoke) {
        localDataSource.insertJoke(map(joke))
    }
}
