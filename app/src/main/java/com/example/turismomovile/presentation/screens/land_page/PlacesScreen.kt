import com.example.turismomovile.presentation.screens.land_page.LangPageViewModel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.turismomovile.presentation.components.InfoRow
import org.koin.compose.koinInject
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.turismomovile.presentation.components.BottomNavigationBar
import com.example.turismomovile.presentation.components.MainTopAppBar
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.NotificationType
import com.example.turismomovile.presentation.components.PullToRefreshComponent
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                            viewModel.loadAsociaciones(searchQuery = searchQuery)
                        },
                        onToggleSearch = { isSearchVisible = !isSearchVisible },
                        onCloseSearch = {
                            isSearchVisible = false
                            searchQuery = ""
                            viewModel.loadAsociaciones()
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
                                viewModel.loadAsociaciones()
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
                            onClick = onDismiss,
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