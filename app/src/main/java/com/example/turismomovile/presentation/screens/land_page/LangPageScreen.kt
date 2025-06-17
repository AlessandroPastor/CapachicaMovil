package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.turismomovile.R
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.turismomovile.data.remote.dto.configuracion.SliderMuni
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.InfoItem
import com.example.turismomovile.presentation.components.MainTopAppBar
import com.example.turismomovile.presentation.components.SearchBar
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject(),
    navController: NavController
) {

    val visible = remember { mutableStateOf(false) }
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    var currentSection by remember { mutableStateOf(LangPageViewModel.Sections.HOME) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    fun scrollToSection(section: LangPageViewModel.Sections) {
        viewModel.onSectionSelected(section)

        if (section != LangPageViewModel.Sections.PRODUCTS) {
            coroutineScope.launch {
                when (section) {
                    LangPageViewModel.Sections.SERVICES -> lazyListState.animateScrollToItem(1)
                    LangPageViewModel.Sections.PLACES -> lazyListState.animateScrollToItem(2)
                    LangPageViewModel.Sections.EVENTS -> lazyListState.animateScrollToItem(3)
                    LangPageViewModel.Sections.RECOMMENDATIONS -> lazyListState.animateScrollToItem(4)
                    else -> Unit
                }
            }
        }
    }

    val municipalidadDescriptionState by viewModel.municipalidadDescriptionState.collectAsState()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stateAso by viewModel.stateAso.collectAsStateWithLifecycle()
    val isRefreshing = remember { mutableStateOf(false) }
    var selectedAsociacion by remember { mutableStateOf<Asociacion?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val sliderImages by viewModel.sliderImagesState.collectAsState()
    val favoriteItems = remember { mutableSetOf<String>() }

    LaunchedEffect(Unit) {
        delay(300)
        visible.value = true
    }

    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    LaunchedEffect(stateAso.notification) {
        if (stateAso.notification.isVisible) {
            notificationState.showNotification(
                message = stateAso.notification.message,
                type = stateAso.notification.type,
                duration = stateAso.notification.duration
            )
        }
    }

    fun handleRefresh() {
        isRefreshing.value = true
        viewModel.refreshMunicipalidades()
        isRefreshing.value = false
    }

    // ✅ AQUÍ VIENE EL CAMBIO IMPORTANTE: usamos nuestro nuevo TopAppBar
    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = state.items.firstOrNull()?.distrito ?: "Bienvenido",
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { /* Implementar búsqueda si deseas */ },
                    onToggleSearch = { isSearchVisible = true },
                    onCloseSearch = { isSearchVisible = false },
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
                        if (section != LangPageViewModel.Sections.PRODUCTS) {
                            coroutineScope.launch {
                                when (section) {
                                    LangPageViewModel.Sections.SERVICES -> lazyListState.animateScrollToItem(1)
                                    LangPageViewModel.Sections.PLACES -> lazyListState.animateScrollToItem(2)
                                    LangPageViewModel.Sections.EVENTS -> lazyListState.animateScrollToItem(3)
                                    LangPageViewModel.Sections.RECOMMENDATIONS -> lazyListState.animateScrollToItem(4)
                                    else -> Unit
                                }
                            }
                        }
                    },
                    navController = navController
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                PullToRefreshComponent(
                    isRefreshing = isRefreshing.value,
                    onRefresh = { handleRefresh() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Slider de imágenes con efecto parallax
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + scaleIn(initialScale = 0.9f)
                            ) {
                                ParallaxImageSlider(
                                    sliders = sliderImages,
                                    title = "Explora Capachica",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Mostrar descripciones de la municipalidad
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally { it * 2 }
                            ) {
                                if (municipalidadDescriptionState.descriptions.isNotEmpty()) {
                                    municipalidadDescriptionState.descriptions.forEach { description ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 16.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    description.logo?.let {
                                                        Image(
                                                            painter = rememberImagePainter(it),
                                                            contentDescription = "Logo de la municipalidad",
                                                            modifier = Modifier
                                                                .size(60.dp)
                                                                .clip(CircleShape)
                                                                .background(
                                                                    MaterialTheme.colorScheme.primary.copy(
                                                                        alpha = 0.2f
                                                                    )
                                                                ),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }

                                                    Text(
                                                        text = state.items.firstOrNull()?.distrito ?: "Municipalidad",
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }

                                                description.descripcion?.let {
                                                    Text(
                                                        text = it,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            lineHeight = 24.sp
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                                        modifier = Modifier.padding(bottom = 16.dp)
                                                    )
                                                }

                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                    ) {
                                                        description.direccion?.let {
                                                            InfoItem(
                                                                label = "Dirección",
                                                                value = it,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }

                                                        description.ruc?.let {
                                                            InfoItem(
                                                                label = "RUC",
                                                                value = it,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                    }

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                    ) {
                                                        description.correo?.let {
                                                            InfoItem(
                                                                label = "Correo",
                                                                value = it,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }

                                                        description.nombre_alcalde?.let {
                                                            InfoItem(
                                                                label = "Alcalde",
                                                                value = it,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                    }

                                                    description.anio_gestion?.let {
                                                        InfoItem(
                                                            label = "Año de gestión",
                                                            value = it,
                                                            modifier = Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Text(
                                            text = "No hay información de la municipalidad disponible.",
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }

                        // Lugares destacados - Convertido a items individuales
                        item {
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + scaleIn(initialScale = 0.9f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Lugares Emblemáticos",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )

                                        Text(
                                            text = "Ver todos",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.primary
                                            ),
                                            modifier = Modifier.clickable { /* Navegar a todos */ }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        // Mostrar las asociaciones como items individuales en pares (2 columnas)
                        val associations = stateAso.itemsAso
                        val chunkedAssociations = associations.chunked(2)

                        items(chunkedAssociations) { rowAssociations ->
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    rowAssociations.forEach { asociacion ->
                                        val isFavorite = favoriteItems.contains(asociacion.id)
                                        AssociationCard(
                                            asociacion = asociacion,
                                            isFavorite = isFavorite,
                                            onFavoriteClick = {
                                                if (isFavorite) {
                                                    favoriteItems.remove(asociacion.id)
                                                } else {
                                                    asociacion.id?.let { favoriteItems.add(it) }
                                                }
                                            },
                                            onClick = { selectedAsociacion = asociacion },
                                            modifier = Modifier
                                                .weight(1f)
                                                .animateContentSize(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    )
                                                )
                                        )
                                    }

                                    // Si solo hay una asociación en la fila, agregar un Spacer para equilibrar
                                    if (rowAssociations.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))

                            // Eventos próximos
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally { -it }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    Text(
                                        text = "Eventos Próximos",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    // Placeholder para eventos - puedes reemplazar con tu implementación
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Eventos próximos",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            // Recomendaciones
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    Text(
                                        text = "Recomendados para ti",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )

                                    // Placeholder para recomendaciones - puedes reemplazar con tu implementación
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Recomendaciones",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Diálogo de detalle con animación
                selectedAsociacion?.let { asociacion ->
                    AssociationDetailDialog(
                        association = asociacion,
                        isFavorite = favoriteItems.contains(asociacion.id),
                        onFavoriteClick = {
                            if (favoriteItems.contains(asociacion.id)) {
                                favoriteItems.remove(asociacion.id)
                            } else {
                                asociacion.id?.let { favoriteItems.add(it) }
                            }
                        },
                        onDismiss = { selectedAsociacion = null }
                    )
                }
            }
        }
    }
}


@Composable
fun ParallaxImageSlider(
    sliders: List<SliderMuni>,
    title: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        sliders.size
    }

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val slider = sliders[page]  // Obtener el SliderMuni actual
            val imageUrl = slider.url_images // La URL de la imagen

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .graphicsLayer {
                        // Efecto parallax basado en el offset
                        val absOffset = pageOffset.absoluteValue

                        // Ajustamos la opacidad y escala
                        alpha = 1f - (absOffset * 0.5f)
                        val scale = 0.9f + (1f - absOffset) * 0.1f
                        scaleX = scale
                        scaleY = scale

                        // Efecto de profundidad (parallax)
                        translationX = pageOffset * size.width * 0.5f
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Cargar la imagen desde la URL proporcionada por el backend
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagen turística $page",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(R.drawable.tusi),
                        error = painterResource(R.drawable.tusi)
                    )

                    // Gradiente para mejorar legibilidad del texto
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    ),
                                    startY = 0.5f
                                )
                            )
                    )

                    // Indicadores de página
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(sliders.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (pagerState.currentPage == index) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

