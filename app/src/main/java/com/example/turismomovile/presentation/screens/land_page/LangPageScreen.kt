package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.shadow
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
import io.dev.kmpventas.presentation.screens.land_page.LangPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.koin.compose.koinInject
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.DialogProperties
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stateAso by viewModel.stateAso.collectAsStateWithLifecycle()
    val isRefreshing = remember { mutableStateOf(false) }
    var selectedAsociacion by remember { mutableStateOf<Asociacion?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Favoritos
    val favoriteItems = remember { mutableSetOf<String>() }

    val sliderImages = listOf(
        R.drawable.fondo,
        R.drawable.fondo2,
        R.drawable.capachica,
    )

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
                        state = rememberLazyListState(),
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
                                    images = sliderImages,
                                    title = "Explora Capachica",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Categorías
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                CategoriesRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 24.dp)
                                )
                            }

                            // Descripción con animación
                            AnimatedVisibility(
                                visible = visible.value,
                                enter = fadeIn() + slideInHorizontally { it * 2 }
                            ) {
                                Text(
                                    text = "Descubre experiencias únicas, cultura viva y naturaleza inolvidable en Capachica. Un destino que te sorprenderá en cada rincón.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        lineHeight = 24.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParallaxImageSlider(
    images: List<Int>,
    title: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        images.size
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
                    Image(
                        painter = painterResource(images[page]),
                        contentDescription = "Imagen turística $page",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
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
                            repeat(images.size) { index ->
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

@Composable
fun CategoriesRow(modifier: Modifier = Modifier) {
    val categories = listOf(
        "Naturaleza" to R.drawable.capachica,
        "Cultura" to R.drawable.escallani,
        "Aventura" to R.drawable.isla_tikonata,
        "Gastronomía" to R.drawable.chifron,
        "Alojamiento" to R.drawable.cottos
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { (name, iconRes) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                    .padding(12.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun CategoryItem(name: String, iconRes: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { /* Acción al hacer clic */ }
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.size(60.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
fun GlassEventsSection(
    events: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Eventos Destacados",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            items(events) { (name, imageRes) ->
                GlassEventCard(name = name, imageRes = imageRes)
            }
        }
    }
}

@Composable
fun GlassEventCard(name: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Efecto de cristal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                            ),
                            startY = 0.3f
                        )
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "25 Oct, 2023 • 10:00 AM",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
            }

            // Badge de "Próximo"
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "PRÓXIMO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
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