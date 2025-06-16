package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.turismomovile.R
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.EventCard
import com.example.turismomovile.presentation.components.MainTopAppBar
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject

@Composable
fun EventsScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val currentSection by viewModel.currentSection
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.EVENTS)
    }

    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = "Eventos",
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Agrega lógica de búsqueda de eventos si deseas */ },
                    onToggleSearch = { isSearchVisible = true },
                    onCloseSearch = {
                        isSearchVisible = false
                        searchQuery = ""
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                EventsHorizontalList()
            }
        }
    }
}



@Composable
fun EventsHorizontalList() {
    val events = listOf(
        "Festival de la Vendimia" to R.drawable.fondo,
        "Carnaval Capachiqueño" to R.drawable.fondo2,
        "Feria Artesanal" to R.drawable.capachica,
        "Semana Turística" to R.drawable.fondo
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(events) { (name, imageRes) ->
            EventCard(name = name, imageRes = imageRes)
        }
    }
}
