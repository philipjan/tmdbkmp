package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.domain.entity.movie.MovieData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.test.runTest

class LandingReducerTest {

    private val reducer = LandingReducer()

    @Test
    fun `appends paging data and updates page metadata`() = runTest {
        val initial = LandingState(
            movieData = MovieData(
                page = 1,
                totalPages = 3,
                movies = listOf(Movie(title = "One").apply { id = 1 })
            )
        )

        val intent = LandingIntent.PagingDataAppended(
            page = 2,
            totalPages = 3,
            movies = listOf(Movie(title = "Two").apply { id = 2 })
        )

        val result = reducer.reduce(initial, intent)

        assertEquals(2, result.movieData?.page)
        assertEquals(3, result.movieData?.totalPages)
        assertEquals(2, result.movieData?.movies?.size)
        assertFalse(result.isPagingLoading)
    }

    @Test
    fun `initial loading intent toggles state`() = runTest {
        val result = reducer.reduce(LandingState.initial(), LandingIntent.InitialLoadingChanged(true))

        assertEquals(true, result.isLoading)
        assertFalse(result.isPagingLoading)
    }
}
