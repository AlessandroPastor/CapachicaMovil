import androidx.compose.animation.animateContentSize
import com.example.turismomovile.presentation.screens.land_page.LangPageViewModel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import org.koin.compose.koinInject
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.MainTopAppBar
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.NotificationType
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.TourismMessageType
import com.example.turismomovile.presentation.components.TourismWhatsAppButton
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.turismomovile.R
import com.example.turismomovile.presentation.components.WhatsAppFloatingButton

@Composable
fun PlacesScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    // Estados
    val lazyListState = rememberLazyListState()
    var isBottomNavVisible by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Variables para detectar dirección del scroll
    var previousScrollOffset by remember { mutableStateOf(0) }
    var scrollDirection by remember { mutableStateOf(LangPageViewModel.ScrollDirection.NONE) }
    // Detectar dirección del scroll mejorado
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemScrollOffset
        }.collect { currentScrollOffset ->
            val scrollDifference = currentScrollOffset - previousScrollOffset

            scrollDirection = when {
                scrollDifference > 50 -> LangPageViewModel.ScrollDirection.DOWN // Scroll hacia abajo
                scrollDifference < -50 -> LangPageViewModel.ScrollDirection.UP   // Scroll hacia arriba
                else -> scrollDirection // Mantener dirección actual
            }

            // Controlar visibilidad basado en la dirección y posición
            isBottomNavVisible = when {
                lazyListState.firstVisibleItemIndex == 0 &&
                        currentScrollOffset < 50 -> true // Mostrar en el top
                scrollDirection == LangPageViewModel.ScrollDirection.UP -> true  // Mostrar al scroll hacia arriba
                scrollDirection == LangPageViewModel.ScrollDirection.DOWN -> false // Ocultar al scroll hacia abajo
                else -> isBottomNavVisible // Mantener estado actual
            }

            previousScrollOffset = currentScrollOffset
        }
    }

    // Estados para las notificaciones
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val currentSection by viewModel.currentSection
    val stateAso by viewModel.stateAso.collectAsStateWithLifecycle()

    // Variables para manejar favoritos y refresco
    val favoriteItems = remember { mutableSetOf<String>() }
    var selectedAsociacion by remember { mutableStateOf<Asociacion?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Efecto para animaciones y notificaciones de bienvenida
    LaunchedEffect(Unit) {
        // Mostrar notificación de bienvenida después de que cargue el layout
        delay(500)
        notificationState.showNotification(
            message = "¡Lugares EXPECTACULARES!",
            type = NotificationType.SUCCESS,
            duration = 3500
        )
    }

    // Manejo de notificaciones del estado
    LaunchedEffect(stateAso.notification) {
        if (stateAso.notification.isVisible) {
            notificationState.showNotification(
                message = stateAso.notification.message,
                type = stateAso.notification.type,
                duration = stateAso.notification.duration
            )
        }
    }
// Controlar el estado de refresh con feedback
    LaunchedEffect(stateAso.isLoading, stateAso.isLoading) {
        if (!stateAso.isLoading && !stateAso.isLoading && isRefreshing) {
            isRefreshing = false
            notificationState.showNotification(
                message = "Datos actualizados correctamente",
                type = NotificationType.SUCCESS,
                duration = 2000
            )
        }
    }
    // UI
    AppTheme(darkTheme = isDarkMode) {
        NotificationHost(state = notificationState) {
            Scaffold(
                topBar = {
                    MainTopAppBar(
                        title = "Lugares Emblemáticos",
                        isSearchVisible = isSearchVisible,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            viewModel.loadAsociacionesLAND(name = searchQuery)
                        },
                        onToggleSearch = { isSearchVisible = !isSearchVisible },
                        onCloseSearch = {
                            isSearchVisible = false
                            searchQuery = ""
                            viewModel.loadAsociacionesLAND()
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
                        navController = navController,
                        isVisible = isBottomNavVisible // Controlando la visibilidad con el estado
                    )
                }
            ) { innerPadding ->
                // Usamos PullToRefreshComponent para envolver el contenido
                PullToRefreshComponent(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        coroutineScope.launch {
                            try {
                                viewModel.loadAsociacionesLAND()
                                viewModel.loadImgAsoaciones()
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        when {
                            stateAso.isLoading -> {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }

                            stateAso.error != null -> {
                                Text(
                                    text = "Error al cargar asociaciones: ${stateAso.error}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            else -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Título de la lista de asociaciones
                                    Text(
                                        text = "Asociaciones Disponibles",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(stateAso.itemsAso) { asociacion ->
                                            val isFavorite = favoriteItems.contains(asociacion.id)
                                            AssociationCard(
                                                asociacion = asociacion,
                                                isFavorite = isFavorite,
                                                onFavoriteClick = {
                                                    if (isFavorite) favoriteItems.remove(asociacion.id)
                                                    else asociacion.id?.let { favoriteItems.add(it) }
                                                },
                                                onClick = { selectedAsociacion = asociacion }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Mostrar el detalle de la asociación si es seleccionado
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
    }
}



@Composable
fun AssociationCard(
    asociacion: Asociacion,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { asociacion.imagenes?.size ?: 0 })
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Card(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (isPressed) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        interactionSource = interactionSource
    )
    {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Botón de favorito con animación
            IconButton(
                onClick = {
                    onFavoriteClick()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .zIndex(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .size(36.dp)
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
                // Nombre con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Text(
                        text = asociacion.nombre ?: "Sin nombre",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .align(Alignment.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Pager con efecto de borde
                Box(
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = asociacion.imagenes?.get(page)?.url_image,
                                    placeholder = ColorPainter(MaterialTheme.colorScheme.surface)
                                ),
                                contentDescription = "Imagen ${page + 1} de ${asociacion.nombre}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay oscuro en la parte inferior para mejorar legibilidad
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .align(Alignment.BottomStart)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                // Indicadores de página más visibles
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    asociacion.imagenes?.let {
                        repeat(it.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = if (pagerState.currentPage == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = if (pagerState.currentPage == index) 1.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }

                // Rating y ubicación con mejor espaciado
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating con más información
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "4.8 (128)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Text(
                            text = "Excelente",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Ubicación con icono más destacado
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = asociacion.lugar ?: "Desconocido",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "2.5 km",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp)
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
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .animateContentSize(), // ✨ Animación suave
            shape = RoundedCornerShape(24.dp), // 🎨 Esquinas más elegantes
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 32.dp, // 🌟 Sombra más dramática
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 🖼️ Header con imagen mejorado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp) // Un poco más alto
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    // Imagen con overlay gradiente
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = association.imagenes?.firstOrNull()?.url_image,
                                placeholder = painterResource(R.drawable.escallani) // 🖼️ Placeholder bonito
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // 🌅 Gradiente overlay para mejor legibilidad
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.5f)
                                        )
                                    )
                                )
                        )
                    }

                    // 🎯 Botones flotantes con efectos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 💖 Botón de favorito mejorado
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    shape = CircleShape
                                )
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                                tint = if (isFavorite) Color(0xFFFF4757) // ❤️ Rojo más bonito
                                else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // ❌ Botón de cerrar mejorado
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    shape = CircleShape
                                )
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // 📝 Contenido mejorado
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 📛 Título con efecto
                    Text(
                        text = association.nombre ?: "Destino Increíble",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // ⭐ Rating y ubicación mejorados
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🌟 Rating con estrellas bonitas
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < 4) Color(0xFFFFD700) // ⭐ Dorado perfecto
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "4.8 • 128 reseñas",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // 📍 Ubicación con chip bonito
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = association.lugar ?: "Ubicación especial",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(start = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // 📋 Descripción con mejor formato
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = association.descripcion ?: "Descubre este increíble destino lleno de aventuras y experiencias únicas que te dejarán recuerdos inolvidables.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 26.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // 📞 Información de contacto mejorada
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Información de contacto",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            association.office_hours?.let {
                                InfoRow(
                                    icon = Icons.Default.AccessTime, // 🕐 Mejor icono para horario
                                    text = "Horario: $it",
                                    iconColor = MaterialTheme.colorScheme.secondary
                                )
                            }

                            association.phone?.let {
                                InfoRow(
                                    icon = Icons.Default.Phone, // 📞 Mejor icono para teléfono
                                    text = "Teléfono: $it",
                                    iconColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🗺️ Botón Ver en mapa
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver en mapa")
                        }

                        // ✅ Aquí va tu botón redondo tal cual es
                        Box(
                            modifier = Modifier
                                .size(64.dp) // 👈 Exactamente igual que el botón
                        ) {
                            WhatsAppFloatingButton(
                                phoneNumber = association.phone ?: "+51963378995",
                                modifier = Modifier.fillMaxSize(), // 🔥 Para que ocupe toda la caja
                                isVisible = true
                            )
                        }
                    }
                }
            }
        }
    }
}


// 📋 Componente InfoRow mejorado
@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = iconColor.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(16.dp)
                    .padding(8.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}