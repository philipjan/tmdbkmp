package com.multiplatform.data.repository

import com.multiplatform.data.datasource.remote.TmdbHttpClient
import com.multiplatform.data.db.AppDatabase
import com.multiplatform.data.model.Movie
import com.multiplatform.data.model.base.BaseListResponse
import com.multiplatform.data.model.mapper.MovieMapper
import com.multiplatform.data.model.mapper.toDomain
import com.multiplatform.data.model.mapper.toEntity
import com.multiplatform.data.model.mapper.upsert
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.utility.util.Constants
import com.multiplatform.utility.util.QueryParams
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MovieRepositoryImpl(
    private val client: TmdbHttpClient,
    private val database: AppDatabase,
) : MovieRepository<MovieData> {

    @OptIn(ExperimentalTime::class)
    override suspend fun getMediaLists(endpoint: String, page: Int?) = withContext(Dispatchers.IO) {
        val pageValue = page ?: Constants.INITIAL_PAGE
        val cacheKey = buildCacheKey(endpoint, pageValue)
        val nowMillis = Clock.System.now().toEpochMilliseconds()

        // ── 1. Check cache freshness ─────────────────────────────────────────
        val cacheEntry = database.cacheEntryQueries.selectByCacheKey(cacheKey).executeAsOneOrNull()
        val isCacheFresh = cacheEntry != null && (nowMillis - cacheEntry.cachedAt) < CACHE_TTL_MILLIS

        if (isCacheFresh) {
            val entities = database.movieQueries.selectByPage(pageValue.toLong()).executeAsList()
            if (entities.isNotEmpty()) {
                // ── 2. Serve from local DB ───────────────────────────────────
                return@withContext MovieData(
                    page = pageValue,
                    totalPages = cacheEntry.totalPages?.toInt(),
                    movies = entities.toDomain(),
                )
            }
        }

        // ── 3. Cache miss / stale — fetch from network ───────────────────────
        val response = client.getClient().get(
            urlString = endpoint,
            block = {
                url {
                    parameters.append(QueryParams.QUERY_PARAM_PAGE, pageValue.toString())
                }
            }
        ).body<BaseListResponse<Movie>>()

        val movieData = MovieMapper().mapTo(response)

        // ── 4. Persist movies + update cache metadata in a single transaction ─
        database.transaction {
            database.movieQueries.deleteByPage(pageValue.toLong())
            movieData.movies.forEach { movie ->
                database.movieQueries.upsert(movie.toEntity(pageValue))
            }
            database.cacheEntryQueries.upsert(
                cacheKey = cacheKey,
                cachedAt = nowMillis,
                totalPages = movieData.totalPages?.toLong(),
            )
        }

        movieData
    }

    private fun buildCacheKey(endpoint: String, page: Int): String = "$endpoint|$page"

    companion object {
        /** Data older than 1 hour is considered stale and triggers a network refresh. */
        private const val CACHE_TTL_MILLIS = 60L * 60L * 1_000L
    }
}