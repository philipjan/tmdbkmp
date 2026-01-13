package com.multiplatform.themoviedatabase.screens.landing

import androidx.lifecycle.ViewModel
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository

class LandingViewModel(private val movieRepository: MovieRepository<MovieData>) : ViewModel() {

}