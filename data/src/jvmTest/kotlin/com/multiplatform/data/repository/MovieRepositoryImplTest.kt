package com.multiplatform.data.repository

import com.multiplatform.data.datasource.remote.TmdbHttpClient
import com.multiplatform.utility.util.QueryParams
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [MovieRepositoryImpl].
 *
 * [TmdbHttpClient] is a singleton holding a private lazy `HttpClient`. A JVM reflection
 * helper resets that field before each test so each test drives its own [MockEngine],
 * keeping tests fully isolated with zero real network I/O.
 */
class MovieRepositoryImplTest {

    private val testToken = "test-bearer-token"

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Builds an [HttpClient] backed by [engine] with the same JSON config used in
     * production so that [kotlinx.serialization] can deserialize mock responses.
     */
    private fun mockHttpClient(engine: MockEngine) = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    /**
     * Uses JVM reflection to overwrite the private `client` field inside the
     * [TmdbHttpClient] singleton so that [MovieRepositoryImpl] calls our mock engine.
     */
    private fun injectMockClient(httpClient: HttpClient) {
        val field = TmdbHttpClient::class.java.getDeclaredField("client")
        field.isAccessible = true
        field.set(TmdbHttpClient, httpClient)
    }

    /** Resets the `client` lateinit field so the next test starts from a clean state. */
    private fun resetTmdbClient() {
        val field = TmdbHttpClient::class.java.getDeclaredField("client")
        field.isAccessible = true
        // Reassign to null via reflection — Kotlin's lateinit backing is a nullable Object slot
        @Suppress("UNCHECKED_CAST")
        (field as java.lang.reflect.Field).set(TmdbHttpClient, null)
    }

    @BeforeTest
    fun setUp() = resetTmdbClient()

    @AfterTest
    fun tearDown() = resetTmdbClient()

    // ── test fixtures ─────────────────────────────────────────────────────────

    private fun movieListJson(
        page: Int = 1,
        totalPages: Int = 10,
        totalResults: Int = 200
    ) = """
        {
          "page": $page,
          "total_pages": $totalPages,
          "total_results": $totalResults,
          "results": [
            {
              "id": 101,
              "title": "Interstellar",
              "original_title": "Interstellar Original",
              "release_date": "2014-11-07",
              "video": false,
              "adult": false,
              "backdrop_path": "/backdrop.jpg",
              "genre_ids": [18, 878],
              "original_language": "en",
              "overview": "A journey beyond the stars.",
              "popularity": 250.5,
              "poster_path": "/poster.jpg",
              "vote_average": 8.6,
              "vote_count": 32000
            }
          ]
        }
    """.trimIndent()

    private fun emptyListJson() = """
        {
          "page": 1,
          "total_pages": 0,
          "total_results": 0,
          "results": []
        }
    """.trimIndent()

    private fun nullResultsJson() = """
        {
          "page": 1,
          "total_pages": 1,
          "total_results": null
        }
    """.trimIndent()

    // ── getMediaLists – happy path ────────────────────────────────────────────

    @Test
    fun getMediaLists_withValidEndpoint_returnsCorrectlyMappedMovieData() = runBlocking {
        // Arrange
        val engine = MockEngine { respond(movieListJson(), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())) }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act
        val result = repo.getMediaLists(endpoint = "movie/popular", page = null)

        // Assert
        assertEquals(1, result.page)
        assertEquals(10, result.totalPages)
        assertEquals(1, result.movies.size)
        with(result.movies.first()) {
            assertEquals(101, id)
            assertEquals("Interstellar", title)
            assertEquals("Interstellar Original", originalTitle)
            assertEquals("2014-11-07", releaseDate)
            assertEquals(false, video)
            assertEquals(false, adult)
            assertEquals("/backdrop.jpg", backdropPath)
            assertEquals(listOf(18, 878), genreIds)
            assertEquals("en", originalLanguage)
            assertEquals("A journey beyond the stars.", overview)
            assertEquals(250.5, popularity)
            assertEquals("/poster.jpg", posterPath)
            assertEquals(8.6, voteAverage)
            assertEquals(32000, voteCount)
        }
    }

    @Test
    fun getMediaLists_withPageProvided_appendsPageQueryParameter() = runBlocking {
        // Arrange
        var capturedUrl = ""
        val engine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(movieListJson(page = 2), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
        }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act
        val result = repo.getMediaLists(endpoint = "movie/popular", page = 2)

        // Assert
        assertTrue(
            capturedUrl.contains("${QueryParams.QUERY_PARAM_PAGE}=2"),
            "Expected URL to contain page=2 but was: $capturedUrl"
        )
        assertEquals(2, result.page)
    }

    @Test
    fun getMediaLists_withNullPage_omitsPageQueryParameter() = runBlocking {
        // Arrange
        var capturedUrl = ""
        val engine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(movieListJson(), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
        }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act
        repo.getMediaLists(endpoint = "movie/popular", page = null)

        // Assert
        assertTrue(
            !capturedUrl.contains(QueryParams.QUERY_PARAM_PAGE),
            "Expected URL to NOT contain 'page' param but was: $capturedUrl"
        )
    }

    // ── getMediaLists – edge cases ────────────────────────────────────────────

    @Test
    fun getMediaLists_withEmptyResults_returnsEmptyMovieList() = runBlocking {
        // Arrange
        val engine = MockEngine { respond(emptyListJson(), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())) }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act
        val result = repo.getMediaLists(endpoint = "movie/popular", page = null)

        // Assert
        assertTrue(result.movies.isEmpty())
        assertEquals(0, result.totalPages)
    }

    @Test
    fun getMediaLists_withMissingResultsKey_returnsEmptyMovieList() = runBlocking {
        // Arrange
        val engine = MockEngine { respond(nullResultsJson(), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())) }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act
        val result = repo.getMediaLists(endpoint = "movie/popular", page = null)

        // Assert
        assertTrue(result.movies.isEmpty())
    }

    @Test
    fun getMediaLists_withMultiplePages_returnsCorrectPageMetadata() = runBlocking {
        // Arrange
        val engine = MockEngine { respond(movieListJson(page = 3, totalPages = 20, totalResults = 400), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())) }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act
        val result = repo.getMediaLists(endpoint = "movie/top_rated", page = 3)

        // Assert
        assertEquals(3, result.page)
        assertEquals(20, result.totalPages)
    }

    @Test
    fun getMediaLists_onServerError_throwsException() = runBlocking {
        // Arrange
        val engine = MockEngine { respond("Internal Server Error", HttpStatusCode.InternalServerError) }
        injectMockClient(mockHttpClient(engine))
        val repo = MovieRepositoryImpl(token = testToken)

        // Act & Assert
        val thrown = assertFailsWith<Exception> {
            repo.getMediaLists(endpoint = "movie/popular", page = null)
        }
        assertTrue(thrown.message?.isNotBlank() ?: true)
        Unit
    }
}


