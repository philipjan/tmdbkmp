package com.multiplatform.themoviedatabase.screens.splash

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.screens.splash.states.SplashState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// ─── Fake ──────────────────────────────────────────────────────────────────────

private class FakeMovieRepository(
    private val response: MovieData? = null,
    private val shouldThrow: Boolean = false,
) : MovieRepository<MovieData> {
    var callCount: Int = 0

    override suspend fun getMediaLists(endpoint: String, page: Int?): MovieData {
        callCount++
        if (shouldThrow) throw RuntimeException("Network error")
        return response ?: throw IllegalStateException("No response configured")
    }
}

// ─── Tests ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testMovieData = MovieData(page = 1, totalPages = 10, movies = emptyList())

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── init block ──────────────────────────────────────────────────────────────

    @Test
    fun init_automatically_dispatchesLoadData_movieDataSetInState() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)

        // Act
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Assert
        assertEquals(testMovieData, viewModel.state.movieData)
    }

    @Test
    fun init_automatically_dispatchesLoadData_isLoadingFalse() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)

        // Act
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.state.isLoading)
    }

    @Test
    fun init_automatically_dispatchesLoadData_repositoryCalledOnce() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)

        // Act
        SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Assert
        assertEquals(1, fakeRepo.callCount)
    }

    @Test
    fun init_repositoryThrows_errorSetInState() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(shouldThrow = true)

        // Act
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Assert
        assertNotNull(viewModel.state.error)
        assertEquals("Network error", viewModel.state.error?.message)
    }

    // ── loadData ────────────────────────────────────────────────────────────────

    @Test
    fun loadData_onSuccess_movieDataSetInState() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle() // drain init

        // Act
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertEquals(testMovieData, viewModel.state.movieData)
    }

    @Test
    fun loadData_onSuccess_isLoadingFalse() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Act
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.state.isLoading)
    }

    @Test
    fun loadData_onSuccess_errorIsNull() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Act
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.state.error)
    }

    // ── Error Path ──────────────────────────────────────────────────────────────

    @Test
    fun loadData_repositoryThrows_errorSetInState() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(shouldThrow = true)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle() // drain init (also throws)

        // Act
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertNotNull(viewModel.state.error)
        assertEquals("Network error", viewModel.state.error?.message)
    }

    @Test
    fun loadData_repositoryThrows_movieDataIsNull() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(shouldThrow = true)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Act
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertNull(viewModel.state.movieData)
    }

    @Test
    fun loadData_repositoryThrows_isLoadingFalse() = runTest(testDispatcher) {
        // Arrange
        val fakeRepo = FakeMovieRepository(shouldThrow = true)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Act
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertEquals(false, viewModel.state.isLoading)
    }

    // ── Edge Cases ──────────────────────────────────────────────────────────────

    @Test
    fun loadData_calledThreeTimes_repositoryCalledFourTimesTotal() = runTest(testDispatcher) {
        // 1 from init + 3 explicit
        val fakeRepo = FakeMovieRepository(response = testMovieData)
        val viewModel = SplashViewModel(movieRepository = fakeRepo, coroutineScope = backgroundScope)
        advanceUntilIdle()

        // Act
        viewModel.loadData()
        viewModel.loadData()
        viewModel.loadData()
        advanceUntilIdle()

        // Assert
        assertEquals(4, fakeRepo.callCount)
    }

    @Test
    fun loadData_customInitialState_coroutinesNotYetAdvanced_stateUnchanged() =
        runTest(testDispatcher) {
            // Arrange
            val fakeRepo = FakeMovieRepository(response = testMovieData)
            val customState = SplashState(isLoading = false, movieData = null)

            // Act — coroutines have NOT advanced yet
            val viewModel = SplashViewModel(movieRepository = fakeRepo, initialState = customState, coroutineScope = backgroundScope)

            // Assert — state is still the customState; no coroutine has run
            assertNull(viewModel.state.movieData)
            assertEquals(false, viewModel.state.isLoading)
        }
}
