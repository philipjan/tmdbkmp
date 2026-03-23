package com.multiplatform.themoviedatabase.screens.splash.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.utility.util.Endpoint
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// ─── Fake ──────────────────────────────────────────────────────────────────────

private class FakeMovieRepository(
    private val response: MovieData? = null,
    private val shouldThrow: Boolean = false,
) : MovieRepository<MovieData> {
    var capturedEndpoint: String? = null
    var capturedPage: Int? = null
    var callCount: Int = 0

    override suspend fun getMediaLists(endpoint: String, page: Int?): MovieData {
        capturedEndpoint = endpoint
        capturedPage = page
        callCount++
        if (shouldThrow) throw RuntimeException("Network error")
        return response ?: throw IllegalStateException("No response configured")
    }
}

// ─── Tests ─────────────────────────────────────────────────────────────────────

class SplashReducerTest {

    private val testMovieData = MovieData(page = 1, totalPages = 5, movies = emptyList())

    // ── Happy Path ──────────────────────────────────────────────────────────────

    @Test
    fun reduce_loadInitialData_repositorySuccess_movieDataSetInState() = runTest {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        // Act
        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        // Assert
        assertEquals(testMovieData, result.movieData)
    }

    @Test
    fun reduce_loadInitialData_repositorySuccess_isLoadingFalse() = runTest {
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertEquals(false, result.isLoading)
    }

    @Test
    fun reduce_loadInitialData_repositorySuccess_errorIsNull() = runTest {
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertNull(result.error)
    }

    @Test
    fun reduce_loadInitialData_callsRepositoryWithPopularEndpoint() = runTest {
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertEquals(Endpoint.ENDPOINT_POPULAR, fakeRepo.capturedEndpoint)
    }

    @Test
    fun reduce_loadInitialData_callsRepositoryWithPageOne() = runTest {
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertEquals(1, fakeRepo.capturedPage)
    }

    // ── Error Path ──────────────────────────────────────────────────────────────

    @Test
    fun reduce_loadInitialData_repositoryThrows_errorSetInState() = runTest {
        val fakeRepo = FakeMovieRepository(shouldThrow = true)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertNotNull(result.error)
        assertEquals("Network error", result.error?.message)
    }

    @Test
    fun reduce_loadInitialData_repositoryThrows_movieDataRemainsNull() = runTest {
        val fakeRepo = FakeMovieRepository(shouldThrow = true)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertNull(result.movieData)
    }

    @Test
    fun reduce_loadInitialData_repositoryThrows_isLoadingFalse() = runTest {
        val fakeRepo = FakeMovieRepository(shouldThrow = true)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertEquals(false, result.isLoading)
    }

    // ── Edge Cases ──────────────────────────────────────────────────────────────

    @Test
    fun reduce_loadInitialData_emptyMovieList_stateMoviesIsEmpty() = runTest {
        val emptyMovieData = MovieData(page = 1, totalPages = 0, movies = emptyList())
        val fakeRepo = FakeMovieRepository(response = emptyMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertNotNull(result.movieData)
        assertTrue(result.movieData!!.movies.isEmpty())
    }

    @Test
    fun reduce_loadInitialData_nullPageResponse_statePageIsNull() = runTest {
        val nullPageData = MovieData(page = null, totalPages = null, movies = emptyList())
        val fakeRepo = FakeMovieRepository(response = nullPageData)
        val reducer = SplashReducer(movieRepository = fakeRepo)

        val result = reducer.reduce(SplashState.initial(), SplashIntent.LoadInitialData)

        assertNull(result.movieData?.page)
        assertNull(result.movieData?.totalPages)
    }

    @Test
    fun reduce_loadInitialData_calledTwice_repositoryInvokedTwice() = runTest {
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)
        val state = SplashState.initial()

        reducer.reduce(state, SplashIntent.LoadInitialData)
        reducer.reduce(state, SplashIntent.LoadInitialData)

        assertEquals(2, fakeRepo.callCount)
    }

    // ── TODO stub ───────────────────────────────────────────────────────────────

    @Test
    fun reduce_loadSucceedingData_throwsNotImplementedError() = runTest {
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val reducer = SplashReducer(movieRepository = fakeRepo)
        var caughtError: Throwable? = null

        try {
            reducer.reduce(SplashState.initial(), SplashIntent.LoadSucceedingData(newPage = 2))
        } catch (e: NotImplementedError) {
            caughtError = e
        }

        assertNotNull(caughtError)
    }
}

