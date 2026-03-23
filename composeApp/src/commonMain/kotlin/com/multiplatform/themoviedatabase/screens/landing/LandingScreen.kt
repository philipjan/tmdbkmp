package com.multiplatform.themoviedatabase.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.themoviedatabase.component.Media
import com.multiplatform.themoviedatabase.screens.landing.states.LandingState
import com.multiplatform.utility.util.Utility.generateImageUrl

@Composable
fun LandingScreen(
    landingState: LandingState,
    onMediaClicked: (Movie) -> Unit,
    initialConfig: (() -> Unit)? = null,
    loadNext: () -> Unit
) {
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        initialConfig?.invoke()
    }

    // derivedStateOf limits recomposition: only updates when the boolean result changes,
    // not on every scroll offset tick — avoids the snapshotFlow overhead.
    val reachedBottom by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= totalItems - 3
        }
    }

    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !landingState.isLoading && !landingState.isPagingLoading) {
            loadNext()
        }
    }

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

        if (landingState.isPagingLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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