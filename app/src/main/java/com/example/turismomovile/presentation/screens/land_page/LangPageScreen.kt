package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.turismomovile.R
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescription
import com.example.turismomovile.data.remote.dto.configuracion.SliderMuni
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.MainTopAppBar
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.NotificationType
import com.example.turismomovile.presentation.components.StatCard
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


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
        lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    )
    val currentSection by remember { mutableStateOf(LangPageViewModel.Sections.HOME) }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val municipalidadDescriptionState by viewModel.municipalidadDescriptionState.collectAsState()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val sliderImages by viewModel.sliderImagesState.collectAsState()

    // Efecto para animaciones y notificaciones de bienvenida
    LaunchedEffect(Unit) {
        // Mostrar notificación de bienvenida después de que cargue el layout
        delay(500)
        notificationState.showNotification(
            message = "¡Bienvenido a ${state.items.firstOrNull()?.distrito ?: "Turismo Movile"}!",
            type = NotificationType.SUCCESS,
            duration = 3500
        )

        // Activar animaciones de contenido con timing escalonado
        delay(1000)
        visible.value = true
    }

    // Manejo de notificaciones del estado
    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    // Manejo de notificaciones para stateAso
    LaunchedEffect(municipalidadDescriptionState.notification) {
        if (municipalidadDescriptionState.notification.isVisible) {
            notificationState.showNotification(
                message = municipalidadDescriptionState.notification.message,
                type = municipalidadDescriptionState.notification.type,
                duration = municipalidadDescriptionState.notification.duration
            )
        }
    }

    // Controlar el estado de refresh con feedback
    LaunchedEffect(state.isLoading, municipalidadDescriptionState.isLoading) {
        if (!state.isLoading && !municipalidadDescriptionState.isLoading && isRefreshing) {
            isRefreshing = false
            notificationState.showNotification(
                message = "Datos actualizados correctamente",
                type = NotificationType.SUCCESS,
                duration = 2000
            )
        }
    }

    AppTheme(darkTheme = isDarkMode) {
        NotificationHost(state = notificationState) {
            Scaffold(
                topBar = {
                    MainTopAppBar(
                        title = state.items.firstOrNull()?.distrito ?: "Municipalidad",
                        isSearchVisible = isSearchVisible,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { /* Implementar búsqueda */ },
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
                                        LangPageViewModel.Sections.SERVICES -> lazyListState.animateScrollToItem(2)
                                        LangPageViewModel.Sections.PLACES -> lazyListState.animateScrollToItem(3)
                                        LangPageViewModel.Sections.EVENTS -> lazyListState.animateScrollToItem(4)
                                        LangPageViewModel.Sections.RECOMMENDATIONS -> lazyListState.animateScrollToItem(5)
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
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            coroutineScope.launch {
                                try {
                                    viewModel.loadMunicipalidad()
                                    viewModel.loadMunicipalidadDescription()
                                } catch (e: Exception) {
                                    notificationState.showNotification(
                                        message = "Error: ${e.message ?: "Intente nuevamente"}",
                                        type = NotificationType.ERROR,
                                        duration = 3000
                                    )
                                    isRefreshing = false
                                }
                            }
                        }
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

                                // Hero Section con animación mejorada
                                AnimatedVisibility(
                                    visible = visible.value,
                                    enter = fadeIn(animationSpec = tween(500)) +
                                            scaleIn(initialScale = 0.9f, animationSpec = tween(500)) +
                                            slideInVertically(animationSpec = tween(500), initialOffsetY = { it / 2 })
                                ) {
                                    MunicipalidadHeroSection(
                                        municipalidad = state.items.firstOrNull(),
                                        description = municipalidadDescriptionState.descriptions.firstOrNull(),
                                        sliderImages = sliderImages
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))

                                // Información detallada con animación escalonada
                                AnimatedVisibility(
                                    visible = visible.value,
                                    enter = fadeIn(animationSpec = tween(delayMillis = 200)) +
                                            slideInHorizontally(animationSpec = tween(400), initialOffsetX = { it * 2 })
                                ) {
                                    if (municipalidadDescriptionState.descriptions.isNotEmpty()) {
                                        municipalidadDescriptionState.descriptions.forEachIndexed { index, description ->
                                            key(description.id) {
                                                MunicipalidadDetailedInfo(
                                                    municipalidad = state.items.firstOrNull(),
                                                    description = description,
                                                    modifier = Modifier.animateEnterExit(
                                                        enter = slideInVertically(
                                                            animationSpec = tween(delayMillis = 100 * index),
                                                            initialOffsetY = { it / 2 }
                                                        ) + fadeIn(animationSpec = tween(delayMillis = 100 * index))
                                                    )
                                                )
                                            }
                                        }
                                    } else {
                                        EmptyMunicipalidadInfo()
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))

                                // Sección de contacto con animación mejorada
                                AnimatedVisibility(
                                    visible = visible.value,
                                    enter = fadeIn() + slideInVertically { it } + scaleIn(initialScale = 0.95f)
                                ) {
                                    municipalidadDescriptionState.descriptions.firstOrNull()?.let { description ->
                                        ContactAndServicesSection(description = description)
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))

                                // Sección de estadísticas con animación mejorada
                                AnimatedVisibility(
                                    visible = visible.value,
                                    enter = fadeIn() + slideInHorizontally { -it } + scaleIn(initialScale = 0.9f)
                                ) {
                                    state.items.firstOrNull()?.let { municipalidad ->
                                        MunicipalidadStatsSection(municipalidad = municipalidad)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun MunicipalidadHeroSection(
    municipalidad: Municipalidad?,
    description: MunicipalidadDescription?,
    sliderImages: List<SliderMuni>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Slider de imágenes de fondo
            if (sliderImages.isNotEmpty()) {
                ParallaxImageSlider(
                    sliders = sliderImages,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Gradiente overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo y nombre de la municipalidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    description?.logo?.let { logoUrl ->
                        AsyncImage(
                            model = logoUrl,
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Column {
                        Text(
                            text = "Municipalidad Distrital de",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = municipalidad?.distrito ?: "Distrito",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }

                // Información del alcalde destacada
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    description?.nombre_alcalde?.let { alcalde ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Alcalde",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Alcalde",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                                Text(
                                    text = alcalde,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                description.anio_gestion?.let { gestion ->
                                    Text(
                                        text = "Gestión $gestion",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MunicipalidadDetailedInfo(
    municipalidad: Municipalidad?,
    description: MunicipalidadDescription,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }
    val elevation by animateDpAsState(
        targetValue = if (interactionSource.collectIsPressedAsState().value) 2.dp else 8.dp,
        label = "cardElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Cambiado a surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Título con decoración
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Información General",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Descripción con animación de aparición
            AnimatedVisibility(
                visible = description.descripcion != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                description.descripcion?.let { desc ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 26.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSurface, // Cambiado a onSurface
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Grid de información mejorado
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primera fila con animación escalonada
                AnimatedVisibility(
                    visible = municipalidad?.provincia != null || municipalidad?.region != null,
                    enter = fadeIn() + slideInHorizontally { it / 2 },
                    exit = fadeOut() + slideOutHorizontally { it / 2 }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        municipalidad?.provincia?.let { provincia ->
                            InfoCardItem(
                                icon = Icons.Default.LocationOn,
                                label = "Provincia",
                                value = provincia,
                                modifier = Modifier.weight(1f),
                                iconTint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        municipalidad?.region?.let { region ->
                            InfoCardItem(
                                icon = Icons.Default.Public,
                                label = "Región",
                                value = region,
                                modifier = Modifier.weight(1f),
                                iconTint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // Elementos individuales con animaciones
                val items = listOfNotNull(
                    description.direccion?.let { Triple(Icons.Default.Place, "Dirección", it) },
                    description.ruc?.let { Triple(Icons.Default.Badge, "RUC", it) },
                    description.correo?.let { Triple(Icons.Default.Email, "Correo", it) }
                )

                items.forEachIndexed { index, (icon, label, value) ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { (index + 1) * 30 },
                        exit = fadeOut() + slideOutVertically { (index + 1) * 30 }
                    ) {
                        InfoCardItem(
                            icon = icon,
                            label = label,
                            value = value,
                            modifier = Modifier.fillMaxWidth(),
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                        if (index < items.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Footer decorativo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}


@Composable
private fun InfoCardItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 2.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alpha(0.8f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ContactAndServicesSection(
    description: MunicipalidadDescription
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Cambiado a surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Contacto y Servicios",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            description.correo?.let { correo ->
                ContactItem(
                    icon = Icons.Default.Email,
                    label = "Correo Electrónico",
                    value = correo,
                    isClickable = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            description.direccion?.let { direccion ->
                ContactItem(
                    icon = Icons.Default.LocationOn,
                    label = "Ubicación",
                    value = direccion,
                    isClickable = false
                )
            }
        }
    }
}


@Composable
fun MunicipalidadStatsSection(
    municipalidad: Municipalidad
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Ubicación Geográfica",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Distrito",
                    value = municipalidad.distrito ?: "N/A",
                    icon = Icons.Default.LocationCity,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Provincia",
                    value = municipalidad.provincia ?: "N/A",
                    icon = Icons.Default.Map,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    label: String,
    value: String,
    isClickable: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isClickable) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = "Abrir",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}



@Composable
fun EmptyMunicipalidadInfo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Sin información",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay información de la municipalidad disponible.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ParallaxImageSlider(
    sliders: List<SliderMuni>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        sliders.size
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val slider = sliders[page]
        val imageUrl = slider.url_images

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val absOffset = pageOffset.absoluteValue
                    alpha = 1f - (absOffset * 0.3f)
                    val scale = 0.95f + (1f - absOffset) * 0.05f
                    scaleX = scale
                    scaleY = scale
                    translationX = pageOffset * size.width * 0.3f
                }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.escallani),
                error = painterResource(R.drawable.escallani)
            )
        }
    }
}