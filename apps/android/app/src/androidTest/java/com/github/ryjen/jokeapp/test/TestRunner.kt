package com.github.ryjen.jokeapp.test

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/** Replace MainApplication so test fixtures exist before discovery and Activity launch. */
class TestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, TestApplication::class.java.name, context)
}
