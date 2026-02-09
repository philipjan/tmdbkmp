package com.multiplatform.themoviedatabase.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.image.LandscapistImage

@Composable
fun Media(
    sourceUrl: String,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit
) {
    LandscapistImage(
        imageModel = { sourceUrl },
        modifier = modifier
            .size(200.dp, 300.dp)
            .clickable(onClick = onClicked),
        imageOptions = ImageOptions(),
        loading = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )
}