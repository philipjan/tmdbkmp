package com.multiplatform.data.model.mapper

import com.multiplatform.data.model.Movie
import com.multiplatform.data.model.base.BaseListResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MovieMapperTest {

    private val mapper = MovieMapper()

    // region mapTo – happy path

    @Test
    fun mapTo_withFullResponse_returnsMappedMovieData() {
        // Arrange
        // data.model.base.BaseMedia fields are `open val` and Movie calls BaseMedia() with no args,
        // so base fields (id, adult, etc.) are always null when constructed in code —
        // they are only populated via JSON deserialization. Only DTO-layer fields are testable here.
        val movie = buildMovie(title = "Inception")
        val response = BaseListResponse(page = 1, results = listOf(movie), totalPages = 10)

        // Act
        val result = mapper.mapTo(response)

        // Assert
        assertEquals(1, result.page)
        assertEquals(10, result.totalPages)
        assertEquals(1, result.movies.size)
        with(result.movies.first()) {
            assertEquals("Inception", title)
            assertEquals("Inception Original", originalTitle)
            assertEquals("2024-01-01", releaseDate)
            assertEquals(false, video)
            // base fields sourced from parent constructor defaults (null in code-constructed instances)
            assertEquals(null, id)
            assertEquals(null, adult)
        }
    }

    @Test
    fun mapTo_withMultipleMovies_mapsAllMovies() {
        // Arrange
        val movies = listOf(buildMovie(title = "Movie A"), buildMovie(title = "Movie B"))
        val response = BaseListResponse(page = 2, results = movies, totalPages = 5)

        // Act
        val result = mapper.mapTo(response)

        // Assert
        assertEquals(2, result.movies.size)
        assertEquals("Movie A", result.movies[0].title)
        assertEquals("Movie B", result.movies[1].title)
    }

    // region mapTo – edge cases

    @Test
    fun mapTo_withNullResponse_returnsEmptyMovieData() {
        // Act
        val result = mapper.mapTo(null)

        // Assert
        assertNotNull(result)
        assertTrue(result.movies.isEmpty())
        assertEquals(null, result.page)
        assertEquals(null, result.totalPages)
    }

    @Test
    fun mapTo_withNullResults_returnsEmptyMovieList() {
        // Arrange
        val response = BaseListResponse<Movie>(page = 1, results = null, totalPages = 1)

        // Act
        val result = mapper.mapTo(response)

        // Assert
        assertTrue(result.movies.isEmpty())
    }

    @Test
    fun mapTo_withEmptyResults_returnsEmptyMovieList() {
        // Arrange
        val response = BaseListResponse<Movie>(page = 1, results = emptyList(), totalPages = 0)

        // Act
        val result = mapper.mapTo(response)

        // Assert
        assertTrue(result.movies.isEmpty())
    }

    @Test
    fun mapTo_withAllNullMovieFields_mapsNullsGracefully() {
        // Arrange
        val emptyMovie = Movie(
            originalTitle = null,
            releaseDate = null,
            title = null,
            video = null
        )
        val response = BaseListResponse(page = 1, results = listOf(emptyMovie), totalPages = 1)

        // Act
        val result = mapper.mapTo(response)

        // Assert
        assertEquals(1, result.movies.size)
        with(result.movies.first()) {
            assertEquals(null, title)
            assertEquals(null, originalTitle)
            assertEquals(null, releaseDate)
            assertEquals(null, video)
            assertEquals(null, id)
            assertEquals(null, adult)
        }
    }

    // region helpers

    private fun buildMovie(title: String = "Test Movie") = Movie(
        originalTitle = "$title Original",
        releaseDate = "2024-01-01",
        title = title,
        video = false
    )
}

