package com.multiplatform.themoviedatabase.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.utility.util.Utility.generateImageUrl
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.image.LandscapistImage
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DetailsScreen(
    movie: Movie
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // ── Backdrop ────────────────────────────────────────────────────────
        BackdropSection(backdropPath = movie.backdropPath)

        // ── Poster + Title block ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PosterImage(
                posterPath = movie.posterPath,
                modifier = Modifier.width(120.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = movie.title.orEmpty(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (!movie.originalTitle.isNullOrBlank() && movie.originalTitle != movie.title) {
                    Text(
                        text = movie.originalTitle.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                movie.releaseDate?.let { date ->
                    MetadataChip(label = date)
                }

                movie.originalLanguage?.let { lang ->
                    MetadataChip(label = lang.uppercase())
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        // ── Stats row ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            movie.voteAverage?.let { avg ->
                val tenths = (avg * 10).roundToInt()
                StatItem(label = "Rating", value = "${tenths / 10}.${abs(tenths % 10)}")
            }
            movie.voteCount?.let { count ->
                StatItem(label = "Votes", value = count.toString())
            }
            movie.popularity?.let { pop ->
                StatItem(label = "Popularity", value = pop.roundToInt().toString())
            }
        }

        // ── Overview ────────────────────────────────────────────────────────
        if (!movie.overview.isNullOrBlank()) {
            val overview = movie.overview.orEmpty()
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Overview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BackdropSection(backdropPath: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        if (!backdropPath.isNullOrBlank()) {
            LandscapistImage(
                imageModel = { backdropPath.generateImageUrl() },
                modifier = Modifier.fillMaxSize(),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop)
            )
        }
    }
}

@Composable
private fun PosterImage(
    posterPath: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        if (!posterPath.isNullOrBlank()) {
            LandscapistImage(
                imageModel = { posterPath.generateImageUrl() },
                modifier = Modifier.fillMaxSize(),
                imageOptions = ImageOptions(contentScale = ContentScale.Crop)
            )
        }
    }
}

@Composable
private fun MetadataChip(label: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}