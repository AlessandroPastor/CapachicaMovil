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
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject(),
) {
    // Cada vez que entra a la pantalla, seteamos el section actual
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.EVENTS, navController)
    }

    val currentSection by viewModel.currentSection
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)

    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Eventos") }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentSection = currentSection,
                    onSectionSelected = { section -> viewModel.onSectionSelected(section, navController) },
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
