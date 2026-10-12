package com.github.ryjen.jokeapp.test.module

import com.github.ryjen.jokeapp.data.arch.module.dataModules
import com.github.ryjen.jokeapp.domain.arch.module.domainModules
import com.github.ryjen.jokeapp.meta.arch.module.metaModules
import com.github.ryjen.jokeapp.test.data.arch.module.fakeDatabaseModule
import com.github.ryjen.jokeapp.ui.arch.module.uiModules

// Load the application graph before test-only overrides.
val fakeAppModules = metaModules + dataModules + domainModules + uiModules +
    listOf(fakeDatabaseModule, fakeDispatcherModule)
