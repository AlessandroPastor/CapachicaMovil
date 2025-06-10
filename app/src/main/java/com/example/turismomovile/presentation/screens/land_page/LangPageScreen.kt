package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.turismomovile.data.remote.dto.configuracion.Service
import com.example.turismomovile.data.remote.dto.configuracion.SliderMuni
import com.example.turismomovile.presentation.components.InfoItem
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {

    val visible = remember { mutableStateOf(false) }
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    // Nuevo estado para la navegación inferior
    var currentSection by remember { mutableStateOf(LangPageViewModel.Sections.HOME) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Función para desplazarse a una sección
    fun scrollToSection(section: LangPageViewModel.Sections) {
        currentSection = section
        coroutineScope.launch {
            when (section) {
                LangPageViewModel.Sections.HOME -> lazyListState.animateScrollToItem(0)
                LangPageViewModel.Sections.SERVICES -> lazyListState.animateScrollToItem(1)
                LangPageViewModel.Sections.PLACES -> lazyListState.animateScrollToItem(2)
                LangPageViewModel.Sections.EVENTS -> lazyListState.animateScrollToItem(3)
                LangPageViewModel.Sections.RECOMMENDATIONS -> lazyListState.animateScrollToItem(4)
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

    // Favoritos
    val favoriteItems = remember { mutableSetOf<String>() }



    // Efectos de animación
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

    AppTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = !isSearchVisible,
                            enter = fadeIn() + slideInHorizontally()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Logo de la municipalidad
                                municipalidadDescriptionState.descriptions.firstOrNull()?.logo?.let { logoUrl ->
                                    Image(
                                        painter = rememberAsyncImagePainter(logoUrl),
                                        contentDescription = "Logo municipalidad",
                                        modifier = Modifier
                                            .size(32.dp) // Tamaño similar al texto titleLarge
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                // Texto del distrito
                                Text(
                                    text = state.items.firstOrNull()?.distrito ?: "Bienvenido",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        shadow = Shadow(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                            offset = Offset(2f, 2f),
                                            blurRadius = 3f
                                        )
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        if (isSearchVisible) {
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                onSearch = { /* Implement search */ },
                                onClose = { isSearchVisible = false },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { isSearchVisible = true },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar"
                                    )
                                }

                                IconButton(
                                    onClick = onClickExplorer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = "Explorar"
                                    )
                                }

                                IconButton(
                                    onClick = { themeViewModel.toggleTheme() },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Cambiar tema"
                                    )
                                }

                                Button(
                                    onClick = onStartClick,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Ingresar", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    currentSection = currentSection,
                    onSectionSelected = { scrollToSection(it) }
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
                        state = lazyListState, // Usamos el estado recordado
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
                                    sliders = sliderImages, // Aquí pasamos la lista de SliderMuni
                                    title = "Explora Capachica",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Sección de Servicios
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                ) {
                                    // Título de la sección
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Línea decorativa izquierda
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(24.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    RoundedCornerShape(2.dp)
                                                )
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Título principal
                                        Text(
                                            text = "Nuestros Servicios",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Ícono decorativo
                                        Icon(
                                            imageVector = Icons.Default.HomeWork,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    // Subtítulo descriptivo
                                    Text(
                                        text = "Explora todos los servicios que tenemos disponibles para ti",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                            lineHeight = 20.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp, vertical = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Categorías
                                    CategoriesRow(
                                        viewModel = viewModel,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
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
                                                // Header con logo y distrito
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 16.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    // Logo de la municipalidad
                                                    description.logo?.let {
                                                        Image(
                                                            painter = rememberImagePainter(it),
                                                            contentDescription = "Logo de la municipalidad",
                                                            modifier = Modifier
                                                                .size(60.dp)
                                                                .clip(CircleShape)
                                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    }

                                                    // Título del distrito
                                                    Text(
                                                        text = state.items.firstOrNull()?.distrito ?: "Municipalidad",
                                                        style = MaterialTheme.typography.titleLarge.copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }

                                                // Descripción principal
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

                                                // Información organizada en grid
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Primera fila: Dirección y RUC
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

                                                    // Segunda fila: Correo y Alcalde
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

                                                    // Tercera fila: Año de gestión
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

                        item {
                            // Lugares destacados
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

                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        modifier = Modifier.height(600.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(stateAso.itemsAso) { asociacion ->
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
                                                modifier = Modifier.animateContentSize(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        item {
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

                                    EventsHorizontalList()
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

                                    RecommendationsGrid()
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

// Componente para la barra de navegación inferior
@Composable
fun BottomNavigationBar(
    currentSection: LangPageViewModel.Sections,
    onSectionSelected: (LangPageViewModel.Sections) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                icon = Icons.Default.HomeWork,
                label = "Inicio",
                isSelected = currentSection == LangPageViewModel.Sections.HOME,
                onClick = { onSectionSelected(LangPageViewModel.Sections.HOME) }
            )

            BottomNavItem(
                icon = Icons.Default.Build,
                label = "Servicios",
                isSelected = currentSection == LangPageViewModel.Sections.SERVICES,
                onClick = { onSectionSelected(LangPageViewModel.Sections.SERVICES) }
            )

            BottomNavItem(
                icon = Icons.Default.LocationOn,
                label = "Lugares",
                isSelected = currentSection == LangPageViewModel.Sections.PLACES,
                onClick = { onSectionSelected(LangPageViewModel.Sections.PLACES) }
            )

            BottomNavItem(
                icon = Icons.Default.Star,
                label = "Eventos",
                isSelected = currentSection == LangPageViewModel.Sections.EVENTS,
                onClick = { onSectionSelected(LangPageViewModel.Sections.EVENTS) }
            )

            BottomNavItem(
                icon = Icons.Default.Favorite,
                label = "Recomendados",
                isSelected = currentSection == LangPageViewModel.Sections.RECOMMENDATIONS,
                onClick = { onSectionSelected(LangPageViewModel.Sections.RECOMMENDATIONS) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
            // Calculamos el offset para el efecto parallax
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesRow(viewModel: LangPageViewModel, modifier: Modifier = Modifier) {
    val stateService = viewModel.stateService.collectAsState().value
    // Control para el BottomSheet
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<Service?>(null) }

    if (stateService.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Cargando servicios...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else if (stateService.error != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Error al cargar servicios: ${stateService.error}",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    } else {
        Box {
            LazyRow(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(stateService.items) { service ->
                    ServiceCard(
                        service = service,
                        onClick = {
                            selectedService = service
                            showBottomSheet = true
                        }
                    )
                }
            }

            // BottomSheet para mostrar detalles
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    modifier = Modifier.fillMaxHeight(0.85f)
                ) {
                    selectedService?.let { service ->
                        ServiceDetails(service = service)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: Service,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen del servicio con placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Accedemos a la primera imagen en la lista de imágenes del servicio
                val imageUrl = service.images.firstOrNull()?.imagen_url
                imageUrl?.let {
                    AsyncImage(
                        model = it, // Usamos la URL de la imagen del servicio
                        contentDescription = service.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.tusi),
                        error = painterResource(R.drawable.tusi)
                    )
                }
            }

            // Nombre del servicio
            Text(
                text = service.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            // Badge con precio o estado
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = service.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
        }
    }
}



@Composable
fun ServiceDetails(service: Service) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Mostrar todas las imágenes en un carrusel horizontal
        if (service.images.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(service.images) { image ->
                    Image(
                        painter = rememberAsyncImagePainter(image.imagen_url),
                        contentDescription = image.description,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Información principal
        Text(
            text = service.name,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = service.category,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descripción
        Text(
            text = "Descripción:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = service.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Emprendedores asociados
        if (service.emprendedores.isNotEmpty()) {
            Text(
                text = "Proveído por:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            service.emprendedores.forEach { emprendedor ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = emprendedor.razon_social,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = emprendedor.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
private fun AssociationCard(
    asociacion: Asociacion,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { asociacion.imagenes?.size ?: 0 })

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Botón de favorito
            IconButton(
                onClick = {
                    onFavoriteClick()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = asociacion.nombre ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(asociacion.imagenes?.get(page)?.url_image),
                            contentDescription = "Imagen ${page + 1} de ${asociacion.nombre}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    asociacion.imagenes?.let {
                        repeat(it.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (pagerState.currentPage == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }
                }

                // Rating y ubicación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "4.8",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = asociacion.lugar ?: "Desconocido",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}





@Composable
fun AssociationDetailDialog(
    association: Asociacion,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 24.dp,
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(association.imagenes?.firstOrNull()?.url_image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Botón de cerrar
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                CircleShape
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Botón de favorito
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                CircleShape
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Contenido
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = association.nombre ?: "Nombre desconocido",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Rating y ubicación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < 4) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "4.8 (128 reseñas)",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = association.lugar ?: "Desconocido",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    // Descripción
                    Text(
                        text = association.descripcion ?: "No hay descripción disponible",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )

                    // Horario y contacto
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoRow(
                            icon = Icons.Default.ShoppingCart,
                            text = "Abierto de 9:00 AM a 6:00 PM"
                        )
                        InfoRow(
                            icon = Icons.Default.Explore,
                            text = "A 15 minutos del centro"
                        )
                    }

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { /* Navegar al mapa */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text("Ver en mapa")
                        }

                        Button(
                            onClick = { /* Llamar o contactar */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Contactar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isActive by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp)
    ) {
        // Fondo con efecto de difuminado
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                ),
            shape = RoundedCornerShape(28.dp),
            color = Color.Transparent,
            shadowElevation = if (isActive) 8.dp else 4.dp,
            border = BorderStroke(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (query.isEmpty() && !isActive) {
                        Text(
                            text = "Buscar lugares, eventos...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isActive = it.isFocused },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearch()
                                focusManager.clearFocus()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            innerTextField()
                        }
                    )
                }

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isActive) {
                    TextButton(
                        onClick = {
                            onClose()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            "Cancelar",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
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
        modifier = Modifier.height(300.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
fun EventCard(name: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp),
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
                            startY = 0.5f
                        )
                    )
            )

            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall.copy(
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