package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.turismomovile.R

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.LoadingOverlay
import com.example.turismomovile.presentation.components.MainTopAppBar
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject


@Composable
fun RecommendationsScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    // Estados
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    val stateRecommendations by viewModel.stateEmprendedor.collectAsState()  // ⚠️ Asumiendo que este estado existe
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val currentSection by viewModel.currentSection

    // Efectos
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.RECOMMENDATIONS)
        viewModel.loadEmprendedores()  // ⚠️ Asumiendo que tienes este método en el ViewModel
    }

    LaunchedEffect(stateRecommendations.isLoading) {
        if (!stateRecommendations.isLoading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(stateRecommendations.notification) {
        stateRecommendations.notification.takeIf { it.isVisible }?.let { notification ->
            notificationState.showNotification(
                message = notification.message,
                type = notification.type,
                duration = notification.duration
            )
        }
    }

    // UI
    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = "Recomendaciones",
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        viewModel.loadEmprendedores(searchQuery.takeIf { it.isNotEmpty() })
                    },
                    onToggleSearch = { isSearchVisible = !isSearchVisible },
                    onCloseSearch = {
                        isSearchVisible = false
                        searchQuery = ""
                        viewModel.loadEmprendedores()
                    },
                    onClickExplorer = onClickExplorer,
                    onStartClick = onStartClick,
                    isDarkMode = isDarkMode,
                    onToggleTheme = { themeViewModel.toggleTheme() }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentSection = currentSection,
                    onSectionSelected = { section ->
                        viewModel.onSectionSelected(section)
                    },
                    navController = navController
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(innerPadding)
            ) {
                PullToRefreshComponent(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.loadEmprendedores(searchQuery.takeIf { it.isNotEmpty() })
                    }
                ) {
                    RecommendationsGrid()
                }

                if (stateRecommendations.isLoading && stateRecommendations.items.isEmpty()) {
                    LoadingOverlay()
                }
            }
        }
    }
}




@Composable
fun RecommendationsGrid() {
    val recommendations = listOf(
        "Restaurante Lago Azul" to R.drawable.fondo,
        "Mirador del Sol" to R.drawable.fondo2,
        "Playa Escondida" to R.drawable.capachica,
        "Museo Local" to R.drawable.fondo,
        "Sendero Ecológico" to R.drawable.fondo2,
        "Taller Artesanal" to R.drawable.capachica
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(recommendations) { (name, imageRes) ->
            RecommendationCard(name = name, imageRes = imageRes)
        }
    }
}

@Composable
fun RecommendationCard(name: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0.6f
                        )
                    )
            )

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}


