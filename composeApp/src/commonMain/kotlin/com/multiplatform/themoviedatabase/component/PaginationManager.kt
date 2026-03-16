package com.multiplatform.themoviedatabase.component

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: refactor to support current MVI-Reducer implementation to just plug it when needed
class PaginationManager<T, K>(
    private val coroutineScope: CoroutineScope,
    private var currentPageKey: K,
    private val fetcher: suspend (K) -> List<T>,
    private val nextKeySelector: (List<T>, K) -> K?,
    private val onLoadingChanged: (Boolean) -> Unit,
    private val onDataUpdated: (List<T>) -> Unit
) {
    private val accumulatedData = mutableListOf<T>()
    private var isLoading = false
    private var hasMore = true

    fun getData(): List<T> = accumulatedData.toList()

    fun loadNext() {
        if (isLoading || !hasMore) return
        isLoading = true
        onLoadingChanged(true)

        coroutineScope.launch {
            try {
                val newItems = withContext(Dispatchers.IO) {
                    fetcher(currentPageKey)
                }

                if (newItems.isEmpty()) {
                    hasMore = false
                } else {
                    accumulatedData.addAll(newItems)
                    onDataUpdated(accumulatedData.toList())
                    nextKeySelector(newItems, currentPageKey)?.let { currentPageKey = it }
                }
            } catch (e: Exception) {
                // Handle error logging here
                hasMore = false // Stop on critical error
            } finally {
                isLoading = false
                onLoadingChanged(false)
            }
        }
    }

    fun reset(initialKey: K) {
        accumulatedData.clear()
        currentPageKey = initialKey
        hasMore = true
        onDataUpdated(emptyList())
    }
}