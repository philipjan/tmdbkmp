package com.multiplatform.domain.entity.movie

import com.multiplatform.domain.entity.BaseMedia

data class Movie(
    val originalTitle: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean? = null,
): BaseMedia()