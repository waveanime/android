package com.waveanime.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.waveanime.data.model.SearchAnimeItem
import com.waveanime.data.util.MediaUrl
import com.waveanime.ui.screens.catalog.CatalogScreen
import com.waveanime.ui.screens.home.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(searchViewModel: SearchViewModel = viewModel()) {
    var selectedItem by remember { mutableStateOf(NavItem.HOME) }
    var isSearchActive by remember { mutableStateOf(false) }

    val searchQuery by searchViewModel.query.collectAsState()
    val searchResults by searchViewModel.results.collectAsState()
    val isSearching by searchViewModel.isLoading.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Comportement de défilement : disparaît en scroll bas, réapparaît en scroll haut
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // 2. Gestion du bouton Retour d'Android (ferme la recherche sans quitter l'app)
    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchViewModel.onQueryChange("")
    }

    if (isSearchActive) {
        // Vue Recherche Plein Écran
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth().statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchViewModel.onQueryChange("")
                        }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour")
                        }

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchViewModel.onQueryChange(it) },
                            placeholder = { Text("Rechercher un anime...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                        )

                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.onQueryChange("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Effacer")
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                    Text(
                        text = "Aucun anime trouvé",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(searchResults, key = { it.id }) { anime ->
                            SearchResultRow(anime = anime, onClick = { /* TODO: Détail anime */ })
                        }
                    }
                }
            }
        }
    } else {
        // Détecte si l'utilisateur a scrollé
        val isScrolled = scrollBehavior.state.contentOffset < -10f

        // Transition animée de couleur (Transparent -> Opaque)
        val topBarContainerColor by animateColorAsState(
            targetValue = if (selectedItem == NavItem.HOME && !isScrolled) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            animationSpec = tween(durationMillis = 200),
            label = "TopBarColor"
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Ombre douce en haut (uniquement quand on est au tout début sur le Hero)
                    if (selectedItem == NavItem.HOME && !isScrolled) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.6f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Water,
                                    contentDescription = "Logo",
                                    tint = if (isScrolled || selectedItem != NavItem.HOME) MaterialTheme.colorScheme.primary else Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = if (selectedItem == NavItem.HOME) "WaveAnime" else selectedItem.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = if (isScrolled || selectedItem != NavItem.HOME) MaterialTheme.colorScheme.onSurface else Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = "Rechercher",
                                    tint = if (isScrolled || selectedItem != NavItem.HOME) MaterialTheme.colorScheme.onSurface else Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = topBarContainerColor
                        ),
                        scrollBehavior = scrollBehavior
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 3.dp
                ) {
                    NavItem.entries.forEach { item ->
                        val isSelected = selectedItem == item
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedItem = item },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                AnimatedContent(
                    targetState = selectedItem,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.fillMaxSize(),
                    label = "ScreenSwitch"
                ) { destination ->
                    when (destination) {
                        NavItem.HOME -> HomeScreen()
                        NavItem.WAVELISTS -> PlaceholderScreen("Mes Wavelists")
                        NavItem.CATALOG -> CatalogScreen()
                        NavItem.PLANNING -> PlaceholderScreen("Planning des sorties")
                        NavItem.PROFILE -> PlaceholderScreen("Toi")
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(anime: SearchAnimeItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = MediaUrl.poster(anime.id),
            contentDescription = anime.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(60.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    append(anime.format.uppercase())
                    anime.releaseYear?.let { append(" • $it") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, style = MaterialTheme.typography.titleMedium)
    }
}