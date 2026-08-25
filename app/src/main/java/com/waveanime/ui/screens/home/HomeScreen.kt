package com.waveanime.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.waveanime.data.model.*
import com.waveanime.data.util.MediaUrl
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val uiState = state) {
            is HomeUiState.Loading -> {
                // 1. Loading moderne Shimmer MD3 Expressive
                HomeSkeletonLoading()
            }
            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchHome() }) {
                        Text("Réessayer")
                    }
                }
            }
            is HomeUiState.Success -> {
                HomeContent(data = uiState.data)
            }
        }
    }
}

@Composable
private fun HomeContent(data: HomeResponse) {
    val s = data.selections
    val seasonTitle = buildString {
        append("Aperçu de la saison ")
        append(MediaUrl.getSeasonName(data.currentReleaseDate?.releaseSeason))
        data.currentReleaseDate?.releaseYear?.let { append(" $it") }
    }.trim()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (data.heroes.isNotEmpty()) {
            item { HeroCarousel(heroes = data.heroes) }
        }

        if (s.resumes.isNotEmpty()) {
            item {
                Section(title = "Continuer à regarder") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.resumes, key = { it.id }) { item ->
                            ResumeCard(item = item)
                        }
                    }
                }
            }
        }

        if (s.releasedEpisodes.isNotEmpty()) {
            item {
                Section(title = "Nouveaux épisodes") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.releasedEpisodes, key = { it.id }) { item ->
                            ReleasedEpisodeCard(item = item)
                        }
                    }
                }
            }
        }

        if (s.currentSeason.isNotEmpty()) {
            item {
                Section(title = seasonTitle) {
                    AnimePosterRow(items = s.currentSeason)
                }
            }
        }

        if (s.news.isNotEmpty()) {
            item {
                Section(title = "Les nouveautés") {
                    AnimePosterRow(items = s.news)
                }
            }
        }

        if (s.suggested.isNotEmpty()) {
            item {
                Section(title = "Vous aimerez peut-être...") {
                    AnimePosterRow(items = s.suggested)
                }
            }
        }

        if (s.populars.isNotEmpty()) {
            item {
                Section(title = "Les plus populaires") {
                    AnimePosterRow(items = s.populars)
                }
            }
        }

        if (s.kais.isNotEmpty()) {
            item {
                Section(title = "Par des fans pour les fans") {
                    AnimePosterRow(items = s.kais)
                }
            }
        }

        if (s.movies.isNotEmpty()) {
            item {
                Section(title = "L'animation sur grand écran") {
                    AnimePosterRow(items = s.movies)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// HERO AVEC AUTO-SWIPE (5 SECONDES)
// -------------------------------------------------------------------------

@Composable
private fun HeroCarousel(heroes: List<HeroItem>) {
    val pagerState = rememberPagerState(pageCount = { heroes.size })

    // 2. Auto-Swipe toutes les 5s (sauf si l'utilisateur touche l'écran)
    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(5000)
            if (!pagerState.isScrollInProgress && heroes.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % heroes.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
        ) { page ->
            val hero = heroes[page]
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = MediaUrl.poster(hero.id),
                    contentDescription = hero.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dégradé en haut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Dégradé en bas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                )

                // Textes Hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = hero.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (hero.genres.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            hero.genres.take(3).forEach { genre ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = genre.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    hero.synopsis?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Indicateurs Pager
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(pagerState.pageCount) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(if (isSelected) 22.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// CHARGEMENT EXPRESSIVE SHIMMER SKELETON
// -------------------------------------------------------------------------

@Composable
private fun HomeSkeletonLoading() {
    val transition = rememberInfiniteTransition(label = "Shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerAnim"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .background(shimmerBrush)
        )

        // Titre de section Skeleton
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .width(160.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(shimmerBrush)
        )

        // Ligne de cartes Skeleton
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(shimmerBrush)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPOSANTS DE SECTION & CARTES
// -------------------------------------------------------------------------

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        content()
    }
}

@Composable
private fun ResumeCard(item: ResumeItem) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = MediaUrl.thumbnail(item.id, item.serieId, item.hasThumbnail == 1),
                    contentDescription = item.serieTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (item.duration > 0) {
                    val progress = (item.progressTime / item.duration).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .align(Alignment.BottomCenter),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.serieTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Saison ${item.seasonNumber} • Ép. ${item.number}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReleasedEpisodeCard(item: ReleasedEpisodeItem) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = MediaUrl.thumbnail(item.id, item.serieId, item.hasThumbnail == 1),
                    contentDescription = item.serieTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.serieTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Saison ${item.seasonNumber} • Épisode ${item.number}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnimePosterRow(items: List<AnimeItem>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            AnimePosterCard(item = item)
        }
    }
}

@Composable
private fun AnimePosterCard(item: AnimeItem) {
    Column(
        modifier = Modifier
            .width(125.dp)
            .clickable { },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        ) {
            AsyncImage(
                model = MediaUrl.poster(item.id),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}