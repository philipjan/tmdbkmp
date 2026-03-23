package com.multiplatform.themoviedatabase.screens.landing

import androidx.lifecycle.viewModelScope
import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.MviViewModel
import com.multiplatform.themoviedatabase.component.PaginationManager
import com.multiplatform.themoviedatabase.screens.landing.states.LandingIntent
import com.multiplatform.themoviedatabase.screens.landing.states.LandingReducer
import com.multiplatform.themoviedatabase.screens.landing.states.LandingState
import com.multiplatform.utility.util.Constants
import com.multiplatform.utility.util.Endpoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LandingViewModel(
    private val movieRepository: MovieRepository<MovieData>,
    initialState: LandingState = LandingState.initial(),
    coroutineScope: CoroutineScope? = null
): MviViewModel<LandingState, LandingIntent>(
    reducer = LandingReducer(),
    initialState = initialState,
    coroutineScope = coroutineScope
) {

    private val scope: CoroutineScope = coroutineScope ?: viewModelScope

    private var lastFetchedPage: Int? = initialState.movieData?.page
    private var totalPages: Int? = initialState.movieData?.totalPages
    private var pagedItemsCount: Int = 0

    private val paginationManager = PaginationManager<Movie, Int>(
        coroutineScope = scope,
        currentPageKey = nextPageAfter(initialState.movieData),
        fetcher = { page -> fetchPage(page) },
        nextKeySelector = { _, currentKey -> nextPageKey(currentKey) },
        onLoadingChanged = { isLoading -> dispatch(LandingIntent.PagingLoadingChanged(isLoading)) },
        onDataUpdated = { movies ->
            val newItems = if (movies.size > pagedItemsCount) movies.drop(pagedItemsCount) else emptyList()
            pagedItemsCount = movies.size
            if (newItems.isNotEmpty()) {
                dispatch(
                    LandingIntent.PagingDataAppended(
                        page = lastFetchedPage,
                        totalPages = totalPages,
                        movies = newItems
                    )
                )
            }
        }
    )

    fun loadInitial(movieData: MovieData? = null) {
        scope.launch {
            dispatch(LandingIntent.InitialLoadingChanged(true))
            try {
                val result = movieData ?: movieRepository.getMediaLists(
                    endpoint = Endpoint.ENDPOINT_POPULAR,
                    page = Constants.INITIAL_PAGE
                )
                syncPagination(result)
                dispatch(LandingIntent.InitialDataLoaded(result))
            } catch (e: Exception) {
                dispatch(LandingIntent.Error(e))
            } finally {
                dispatch(LandingIntent.InitialLoadingChanged(false))
            }
        }
    }

    fun loadNext() {
        if (state.isLoading || state.isPagingLoading) return

        val currentPage = state.movieData?.page ?: 0
        val targetPage = if (currentPage == 0) Constants.INITIAL_PAGE else currentPage + 1
        val maxPages = state.movieData?.totalPages

        if (maxPages != null && targetPage > maxPages) return

        pagedItemsCount = 0
        paginationManager.reset(targetPage)
        paginationManager.loadNext()
    }

    fun setInitialData(movieData: MovieData?) {
        if (movieData != null) {
            loadInitial(movieData)
        } else {
            loadInitial()
        }
    }

    private suspend fun fetchPage(page: Int): List<Movie> {
        val maxPages = totalPages
        if (maxPages != null && page > maxPages) return emptyList()

        val result = movieRepository.getMediaLists(
            endpoint = Endpoint.ENDPOINT_POPULAR,
            page = page
        )

        lastFetchedPage = result.page ?: page
        totalPages = result.totalPages ?: totalPages

        return result.movies
    }

    private fun syncPagination(movieData: MovieData) {
        lastFetchedPage = movieData.page
        totalPages = movieData.totalPages
        pagedItemsCount = 0
        paginationManager.reset(nextPageAfter(movieData))
    }

    private fun nextPageAfter(movieData: MovieData?): Int {
        val currentPage = movieData?.page
        return if (currentPage != null) currentPage + 1 else Constants.INITIAL_PAGE
    }

    private fun nextPageKey(currentKey: Int): Int? {
        val maxPages = totalPages
        val next = currentKey + 1
        return if (maxPages != null && next > maxPages) null else next
    }
}