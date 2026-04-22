package com.multiplatform.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class BaseMedia(
    @SerialName("adult")
    open val adult: Boolean? = null,
    @SerialName("backdrop_path")
    open val backdropPath: String? = null,
    @SerialName("genre_ids")
    open val genreIds: List<Int>? = null,
    @SerialName("id")
    open val id: Int? = null,
    @SerialName("original_language")
    open val originalLanguage: String? = null,
    @SerialName("overview")
    open val overview: String? = null,
    @SerialName("popularity")
    open val popularity: Double? = null,
    @SerialName("poster_path")
    open val posterPath: String? = null,
    @SerialName("vote_average")
    open val voteAverage: Double? = null,
    @SerialName("vote_count")
    open val voteCount: Int? = null
)