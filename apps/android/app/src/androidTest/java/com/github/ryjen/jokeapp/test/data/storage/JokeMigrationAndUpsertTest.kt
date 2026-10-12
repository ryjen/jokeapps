package com.github.ryjen.jokeapp.test.data.storage

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.github.ryjen.jokeapp.data.repository.joke.LocalDataSource
import com.github.ryjen.jokeapp.data.storage.Joke
import com.github.ryjen.jokeapp.data.storage.JokeDatabase
import com.github.ryjen.jokeapp.data.storage.datesAdapter
import com.google.common.truth.Truth.assertThat
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

class JokeMigrationAndUpsertTest : KoinTest {
    private val db: JokeDatabase by inject()

    @Test fun upgradesRealVersionOneDatabaseAndPreservesFavoriteState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = "joke-v1-upgrade-" + System.nanoTime() + ".db"
        context.deleteDatabase(file)

        // Model the actual v1 schema, including its nonunique external-key index.
        val old = context.openOrCreateDatabase(file, Context.MODE_PRIVATE, null)
        try {
            old.execSQL("""
                CREATE TABLE joke (
                    id INTEGER PRIMARY KEY,
                    key TEXT NOT NULL,
                    content TEXT NOT NULL,
                    created TEXT DEFAULT CURRENT_TIME,
                    isFavorite INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())
            old.execSQL("CREATE INDEX joke_key ON joke(key)")
            old.execSQL("""
                INSERT INTO joke(id, key, content, created, isFavorite) VALUES
                    (1, 'duplicate-key', 'older favorite', '2020-01-01T00:00:00Z', 1),
                    (2, 'duplicate-key', 'newest content', '2022-02-01T00:00:00Z', 0),
                    (3, 'unique-key', 'independent record', '2023-03-01T00:00:00Z', 1)
            """.trimIndent())
            old.version = 1
        } finally {
            old.close()
        }

        val driver = AndroidSqliteDriver(JokeDatabase.Schema, context, file)
        try {
            val upgraded = JokeDatabase(
                driver = driver,
                jokeAdapter = Joke.Adapter(createdAdapter = datesAdapter())
            )
            val records = upgraded.jokeQueries.list().executeAsList()
            assertThat(JokeDatabase.Schema.version).isEqualTo(2)
            assertThat(records.count { it.key == "duplicate-key" }).isEqualTo(1)
            assertThat(records.size).isEqualTo(2)
            val retained = upgraded.jokeQueries.get("duplicate-key").executeAsOne()
            assertThat(retained.content).isEqualTo("newest content")
            assertThat(retained.isFavorite).isTrue()
            assertThat(retained.created).isNotNull()
            assertThat(upgraded.jokeQueries.get("unique-key").executeAsOne().isFavorite).isTrue()

            // A second insert of the same external key must not introduce duplicates.
            upgraded.jokeQueries.insertOrIgnore("duplicate-key", "stale response", null, false)
            assertThat(upgraded.jokeQueries.list().executeAsList().size).isEqualTo(2)
            assertThat(upgraded.jokeQueries.get("duplicate-key").executeAsOne().isFavorite).isTrue()
        } finally {
            driver.close()
            context.deleteDatabase(file)
        }
    }

    @Test fun simultaneousFavoriteWritesCreateExactlyOneRecord() = runBlocking {
        val key = "atomic-favorite-" + System.nanoTime()
        val local = LocalDataSource(db)
        val joke = Joke(0, key, "a joke", null, false)
        coroutineScope {
            repeat(12) {
                launch(Dispatchers.IO) {
                    local.addFavorite(joke)
                }
            }
        }
        val rows = db.jokeQueries.list().executeAsList().filter { it.key == key }
        assertThat(rows.size).isEqualTo(1)
        assertThat(rows.single().isFavorite).isTrue()

        // Later cache refreshes must not silently erase a favorite.
        local.insertJoke(joke)
        val afterRefresh = db.jokeQueries.get(key).executeAsOne()
        assertThat(afterRefresh.isFavorite).isTrue()
        assertThat(db.jokeQueries.list().executeAsList().count { it.key == key }).isEqualTo(1)
    }
}
