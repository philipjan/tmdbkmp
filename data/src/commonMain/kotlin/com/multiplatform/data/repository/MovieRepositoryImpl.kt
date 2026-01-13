package com.multiplatform.data.repository

import com.multiplatform.data.model.Movie
import com.multiplatform.data.model.base.BaseListResponse
import com.multiplatform.data.model.mapper.MovieMapper
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MovieRepositoryImpl(private val client: HttpClient) : MovieRepository<MovieData> {
    override suspend fun getMediaLists(endpoint: String): MovieData {
        val result: BaseListResponse<Movie>? =  client.get(endpoint).body()
        return MovieMapper().mapTo(result)
    }
}