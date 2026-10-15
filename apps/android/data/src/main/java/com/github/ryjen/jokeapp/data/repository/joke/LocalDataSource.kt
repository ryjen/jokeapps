package com.github.ryjen.jokeapp.data.repository.joke

import com.github.ryjen.jokeapp.data.storage.Joke
import com.github.ryjen.jokeapp.data.storage.JokeDatabase
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers

class LocalDataSource(
    private val db: JokeDatabase
) {
    fun getJoke(id: String) = db.jokeQueries.get(id).executeAsOneOrNull()

    fun getFavoriteJokes(): Flow<List<Joke>> =
        db.jokeQueries.favorites().asFlow().mapToList(Dispatchers.IO)

    fun deleteJoke(joke: Joke) = db.jokeQueries.delete(joke.key)

    fun updateFavorite(key: String, value: Boolean = true) =
        db.jokeQueries.updateFavorite(value, key)

    // Idempotent cache insertion: never replace an existing favorite with a stale remote response.
    fun insertJoke(joke: Joke) =
        db.jokeQueries.insertOrIgnore(
            joke.key,
            joke.content,
            joke.created,
            joke.isFavorite
        )

    // The two operations are one database transaction, with a UNIQUE(key) constraint
    // as the cross-connection guard against concurrent duplicate inserts.
    fun addFavorite(joke: Joke) = db.transaction {
        db.jokeQueries.insertOrIgnore(
            joke.key,
            joke.content,
            joke.created,
            true
        )
        db.jokeQueries.updateFavorite(true, joke.key)
    }

    fun getRandomJoke() = db.jokeQueries.random().executeAsOneOrNull()
}
