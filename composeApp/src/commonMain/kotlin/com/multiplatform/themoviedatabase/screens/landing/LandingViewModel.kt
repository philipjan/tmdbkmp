package com.multiplatform.themoviedatabase.screens.landing

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.MviViewModel
import com.multiplatform.themoviedatabase.screens.landing.states.LandingIntent
import com.multiplatform.themoviedatabase.screens.landing.states.LandingReducer
import com.multiplatform.themoviedatabase.screens.landing.states.LandingState
import kotlinx.coroutines.CoroutineScope

class LandingViewModel(
    movieRepository: MovieRepository<MovieData>,
    initialState: LandingState = LandingState.initial(),
    coroutineScope: CoroutineScope? = null
): MviViewModel<LandingState, LandingIntent>(
    reducer = LandingReducer(movieRepository = movieRepository),
    initialState = initialState,
    coroutineScope = coroutineScope
) {
    init {
//        if (state.movieData == null) {
//            loadInitial()
//        }
    }

    fun loadInitial(movieData: MovieData? = null) {
        dispatch(LandingIntent.LoadInitialData(movieData))
    }

    fun loadNext() {
        dispatch(LandingIntent.LoadSucceedingData(state.movieData?.page))
    }

    fun setInitialData(movieData: MovieData?) {
        if (movieData != null) {
            loadInitial(movieData)
        } else {
            loadInitial()
        }
    }
}