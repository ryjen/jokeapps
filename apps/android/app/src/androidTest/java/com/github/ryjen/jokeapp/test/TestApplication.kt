package com.github.ryjen.jokeapp.test

import android.app.Application
import com.github.ryjen.jokeapp.test.module.fakeAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/** The instrumentation process owns a test-only graph, never the production graph. */
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(fakeAppModules)
        }
    }
}
