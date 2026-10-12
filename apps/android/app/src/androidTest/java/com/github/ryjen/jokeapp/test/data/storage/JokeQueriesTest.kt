package com.github.ryjen.jokeapp.test.data.storage

import com.github.ryjen.jokeapp.data.storage.Joke
import com.github.ryjen.jokeapp.data.storage.JokeDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
class JokeDaoTest : KoinTest {

    private val db: JokeDatabase by inject()

    @Test
    fun testInsertJoke() = runTest {
        val expected = Joke(
            id = 123,
            key = "123",
            content = "testing 123",
            created = java.util.Date(),
            isFavorite = false
        )
        db.jokeQueries.insert(
            key = expected.key,
            content = expected.content,
            expected.created,
            expected.isFavorite
        )
        val joke = db.jokeQueries.get(key = expected.key).executeAsOneOrNull()
        assertThat(joke).isNotNull()
        assertThat(joke?.key).isEqualTo(expected.key)
        assertThat(joke?.created).isNotNull()
    }

    @Test
    fun testDeleteJoke() = runTest {
        val expected = Joke(
            id = 123,
            key = "123-delete",
            content = "testing 123",
            created = null,
            isFavorite = false
        )
        db.jokeQueries.insert(
            key = expected.key,
            content = expected.content,
            expected.created,
            expected.isFavorite
        )
        var joke = db.jokeQueries.get(expected.key).executeAsOneOrNull()
        assertThat(joke).isNotNull()
        db.jokeQueries.delete(expected.key)
        joke = db.jokeQueries.get(expected.key).executeAsOneOrNull()
        assertThat(joke).isNull()
    }

    @Test
    fun externalKeyNeverSelectsOrMutatesAnUnrelatedLocalRowId() = runTest {
        val ownerKey = "collision-owner-" + System.nanoTime()
        db.jokeQueries.insert(ownerKey, "unrelated row", null, false)
        val owner = db.jokeQueries.get(ownerKey).executeAsOne()
        val conflictingExternalKey = owner.id.toString()
        db.jokeQueries.insert(conflictingExternalKey, "desired row", null, false)

        // The external key is numeric, but it must not match the owner's local row ID.
        assertThat(db.jokeQueries.get(conflictingExternalKey).executeAsOne().key)
            .isEqualTo(conflictingExternalKey)
        db.jokeQueries.updateFavorite(true, conflictingExternalKey)
        assertThat(db.jokeQueries.get(ownerKey).executeAsOne().isFavorite).isFalse()
        assertThat(db.jokeQueries.get(conflictingExternalKey).executeAsOne().isFavorite).isTrue()

        db.jokeQueries.delete(conflictingExternalKey)
        assertThat(db.jokeQueries.get(conflictingExternalKey).executeAsOneOrNull()).isNull()
        assertThat(db.jokeQueries.get(ownerKey).executeAsOne().key).isEqualTo(ownerKey)
    }

}
