package com.multiplatform.utility.util

object Endpoint {
    val URL_AUTHENTICATION = "authentication"
    val ENDPOINT_POPULAR = "movie/popular"
    val ENDPOINT_TOPRATED = "movie/top_rated"
    val ENDPOINT_UPCOMING = "movie/upcoming"

    val ENDPOINT_MOVIE_DETAILS ="collection/${Path.PARAM_COLLECTIONID}"
    val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w300"
}

object Path {
    val PARAM_COLLECTIONID = "collection_id"
}

object QueryParams {
    val QUERY_PARAM_PAGE = "page"
}