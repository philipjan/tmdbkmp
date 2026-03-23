package com.multiplatform.domain.entity

import kotlinx.serialization.Serializable

@Serializable
open class BaseMedia(
    open var adult: Boolean? = null,
    open var backdropPath: String? = null,
    open var genreIds: List<Int?>? = null,
    open var id: Int? = null,
    open var originalLanguage: String? = null,
    open var overview: String? = null,
    open var popularity: Double? = null,
    open var posterPath: String? = null,
    open var voteAverage: Double? = null,
    open var voteCount: Int? = null
)