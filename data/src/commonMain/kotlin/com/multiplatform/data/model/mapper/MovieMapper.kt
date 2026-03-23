package com.multiplatform.data.model.mapper

import com.multiplatform.data.model.base.BaseListResponse
import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.data.model.Movie as DataMovie

class MovieMapper : BaseMapper<BaseListResponse<DataMovie>, MovieData> {
    override fun mapTo(param: BaseListResponse<DataMovie>?): MovieData {
        return MovieData(
            page = param?.page,
            totalPages = param?.totalPages,
            movies = buildList {
                param?.results?.forEach {
                    add(
                        Movie(
                            originalTitle = it.originalTitle,
                            releaseDate = it.releaseDate,
                            title = it.title,
                            video = it.video
                        ).apply {
                            this.adult =  it.adult
                            this.backdropPath =  it.backdropPath
                            this.genreIds = it.genreIds
                            this.id = it.id
                            this.originalLanguage = it.originalLanguage
                            this.overview = it.overview
                            this.popularity = it.popularity
                            this.posterPath = it.posterPath
                            this.voteAverage = it.voteAverage
                            this.voteCount = it.voteCount
                        }
                    )
                }
            }
        )
    }
}