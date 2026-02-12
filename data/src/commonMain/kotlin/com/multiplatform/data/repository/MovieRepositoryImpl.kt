package com.multiplatform.data.repository

import com.multiplatform.data.datasource.remote.TmdbHttpClient
import com.multiplatform.data.model.Movie
import com.multiplatform.data.model.base.BaseListResponse
import com.multiplatform.data.model.mapper.MovieMapper
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.utility.util.QueryParams
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(private val client: TmdbHttpClient = TmdbHttpClient) : MovieRepository<MovieData> {
    override suspend fun getMediaLists(endpoint: String, page: Int?) = withContext(Dispatchers.IO) {
        val result = client.client.get(
            urlString = endpoint,
            block = {
                page?.let { pageValue ->
                    url {
                        parameters.append(QueryParams.QUERY_PARAM_PAGE, pageValue.toString())
                    }
                }
            }
        ).body<BaseListResponse<Movie>>()
        MovieMapper().mapTo(result)
    }
}