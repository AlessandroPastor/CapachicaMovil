package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    // Cuando entras, seteamos la sección actual a RECOMMENDATIONS
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.RECOMMENDATIONS, navController)
    }

    val currentSection by viewModel.currentSection
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)

    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Recomendaciones") }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                RecommendationsGrid()
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


