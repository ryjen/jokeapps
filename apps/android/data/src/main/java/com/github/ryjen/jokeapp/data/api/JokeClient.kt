package com.github.ryjen.jokeapp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val JOKE_APP_USER_AGENT = "JokeApps/1.0 (https://github.com/ryjen/jokeapps)"

/** Shared by the Android engine and MockEngine tests to avoid configuration drift. */
internal fun HttpClientConfig<*>.configureJokeClient() {
    // The provider's documented HTTPS endpoints should not redirect to cleartext.
    followRedirects = false

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = false
            encodeDefaults = false
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15000L
        connectTimeoutMillis = 15000L
        socketTimeoutMillis = 15000L
    }
    defaultRequest {
        accept(ContentType.Application.Json)
        header(HttpHeaders.UserAgent, JOKE_APP_USER_AGENT)
    }
}

fun createJokeClient(): HttpClient = HttpClient(Android) {
    configureJokeClient()
}
