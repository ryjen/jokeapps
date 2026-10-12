package com.github.ryjen.jokeapp.data.api

import com.github.ryjen.jokeapp.data.model.JokeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.CancellationException
import timber.log.Timber

private const val BASE_URL = "https://icanhazdadjoke.com"

class JokeService(
    private val client: HttpClient
) {
    suspend fun getRandomJoke(): JokeResponse? = fetch { client.get(BASE_URL) }

    suspend fun getJoke(id: String): JokeResponse? {
        if (id.isBlank()) return null
        return fetch {
            client.get(BASE_URL) {
                // Keep the untrusted identifier in one encoded URL path segment.
                url { appendPathSegments("j", id, encodeSlash = true) }
            }
        }
    }

    private suspend fun fetch(request: suspend () -> HttpResponse): JokeResponse? = try {
        val response = request()
        if (response.status != HttpStatusCode.OK) {
            null
        } else {
            response.body<JokeResponse>().takeIf {
                it.status == HttpStatusCode.OK.value &&
                    it.id.isNotBlank() &&
                    it.joke.isNotBlank()
            }
        }
    } catch (ex: CancellationException) {
        throw ex
    } catch (ex: Exception) {
        Timber.d(ex, "Could not fetch joke")
        null
    }
}
