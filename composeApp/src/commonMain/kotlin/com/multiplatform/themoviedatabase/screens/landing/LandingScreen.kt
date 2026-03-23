package com.multiplatform.themoviedatabase.screens.landing

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.themoviedatabase.component.Media
import com.multiplatform.themoviedatabase.screens.landing.states.LandingState
import com.multiplatform.utility.util.Utility.generateImageUrl
import kotlinx.coroutines.flow.map

@Composable
fun LandingScreen(
    landingState: LandingState,
    onMediaClicked: (Movie) -> Unit,
    initialConfig: (() -> Unit)? = null,
    loadNext: () -> Unit
) {
    val gridState = rememberLazyGridState()
    val pullToRefreshState = rememberPullToRefreshState()
    val isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        initialConfig?.invoke()
    }

    LaunchedEffect(gridState) {
        snapshotFlow {
            gridState.layoutInfo.visibleItemsInfo
        }.map { visibleItems ->
            val lastVisibleItemIndex = visibleItems.lastOrNull()?.index
            val totalItems = gridState.layoutInfo.totalItemsCount
            Pair(totalItems, lastVisibleItemIndex)
        }.collect {
            if (it.first > 0 && it.second != null) {
                if ((it.second ?: 0) >= it.first - 2) {
                    if (!landingState.isLoading) {
                        loadNext()
                    }
                }
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = {},
        state = pullToRefreshState
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState
        ) {
            itemsIndexed(
                items = landingState.movieData?.movies.orEmpty(),
            ) { _, item ->
                key(item.id) {
                    Media(
                        sourceUrl = item.posterPath.orEmpty().generateImageUrl(),
                        onClicked = {
                            onMediaClicked(item)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, name = "Sample UI Preview")
@Composable
fun LandingScreenPreview() {
    LandingScreen(
        landingState = LandingState.initial(),
        onMediaClicked = {_ ->},
        loadNext = {}
    )
}