package com.multiplatform.domain.repository

interface MovieRepository<T> {
    suspend fun getMediaLists(endpoint: String): T
}