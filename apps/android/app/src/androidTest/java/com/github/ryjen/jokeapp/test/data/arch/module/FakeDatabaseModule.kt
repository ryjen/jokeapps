package com.github.ryjen.jokeapp.test.data.arch.module

import com.github.ryjen.jokeapp.data.storage.Joke
import com.github.ryjen.jokeapp.data.storage.JokeDatabase
import com.github.ryjen.jokeapp.data.storage.datesAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val fakeDatabaseModule = module {
    single {
        JokeDatabase(
            driver = AndroidSqliteDriver(JokeDatabase.Schema, androidContext(), null),
            jokeAdapter = Joke.Adapter(
                createdAdapter = datesAdapter()
            )
        )
    }
}
