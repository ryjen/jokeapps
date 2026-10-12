package com.github.ryjen.jokeapp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/** Tests production client configuration through Ktor MockEngine, without networking. */
class JokeServiceTest {
    private fun configured(engine: MockEngine) = HttpClient(engine) { configureJokeClient() }
    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private val good = """{"id":"abc123","joke":"a valid dad joke","status":200}"""

    @Test fun randomUsesHttpsJsonAndDescriptiveAgent() = runBlocking {
        var calls = 0
        val client = configured(MockEngine { request ->
            calls++
            assertEquals("https", request.url.protocol.name)
            assertEquals("icanhazdadjoke.com", request.url.host)
            assertEquals("/", request.url.encodedPath)
            assertEquals("application/json", request.headers[HttpHeaders.Accept])
            assertTrue(request.headers[HttpHeaders.UserAgent].orEmpty().contains("JokeApps/1.0"))
            respond(good, headers = json)
        })
        try {
            val joke = JokeService(client).getRandomJoke()
            assertEquals("abc123", joke?.id)
            assertEquals("a valid dad joke", joke?.joke)
            assertEquals(1, calls)
        } finally { client.close() }
    }

    @Test fun untrustedIdIsOneEncodedPathSegment() = runBlocking {
        val client = configured(MockEngine { request ->
            assertEquals("https", request.url.protocol.name)
            assertEquals("icanhazdadjoke.com", request.url.host)
            assertTrue(request.url.encodedPath.startsWith("/j/"))
            assertTrue(request.url.encodedPath.contains("%2F", ignoreCase = true))
            assertTrue(request.url.encodedPath.contains("%3F", ignoreCase = true))
            assertTrue(request.url.encodedPath.contains("%23", ignoreCase = true))
            assertTrue(request.url.parameters.isEmpty())
            assertTrue(request.url.fragment.isEmpty())
            respond("""{"id":"a/b?c#d","joke":"escaped path joke","status":200}""", headers = json)
        })
        try { assertEquals("a/b?c#d", JokeService(client).getJoke("a/b?c#d")?.id) }
        finally { client.close() }
    }

    @Test fun emptyIdDoesNotMakeRequest() = runBlocking {
        var calls = 0
        val client = configured(MockEngine {
            calls++
            respond(good, headers = json)
        })
        try {
            assertNull(JokeService(client).getJoke("  "))
            assertEquals(0, calls)
        } finally { client.close() }
    }

    @Test fun rejectsHttpErrorsAndInvalidApiPayloads() = runBlocking {
        val samples = listOf(
            HttpStatusCode.NotFound to good,
            HttpStatusCode.OK to """{"id":"x","joke":"no","status":404}""",
            HttpStatusCode.OK to """{"id":"","joke":"no","status":200}""",
            HttpStatusCode.OK to """{"id":"x","joke":"","status":200}""",
            HttpStatusCode.OK to """{"status":200}""",
            HttpStatusCode.OK to "not json"
        )
        for ((status, body) in samples) {
            val client = configured(MockEngine { respond(body, status = status, headers = json) })
            try { assertNull("Should reject status/payload: $status / $body", JokeService(client).getRandomJoke()) }
            finally { client.close() }
        }
    }

    @Test fun doesNotFollowRedirectToInsecureHttp() = runBlocking {
        var calls = 0
        val client = configured(MockEngine {
            calls++
            respond("", status = HttpStatusCode.Found,
                headers = headersOf(HttpHeaders.Location, "http://example.net/joke"))
        })
        try {
            assertNull(JokeService(client).getRandomJoke())
            assertEquals(1, calls)
        } finally { client.close() }
    }

    @Test fun propagatesCoroutineCancellation() {
        val client = configured(MockEngine { throw CancellationException("cancelled by test") })
        try {
            var caught = false
            try { runBlocking { JokeService(client).getRandomJoke() } }
            catch (_: CancellationException) { caught = true }
            assertTrue(caught)
        } finally { client.close() }
    }
}
