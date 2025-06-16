package com.example.turismomovile.presentation.screens.land_page

import android.provider.MediaStore.Images
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turismomovile.data.remote.dto.configuracion.Emprendedor
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorState
import com.example.turismomovile.data.remote.dto.configuracion.Imagen
import com.example.turismomovile.data.remote.dto.configuracion.Producto
import com.example.turismomovile.presentation.components.*
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject

@Composable
fun EmprendedoresScreen(
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
    val stateEmprendedor by viewModel.stateEmprendedor.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val currentSection by viewModel.currentSection
    var selectedEmprendedor by remember { mutableStateOf<Emprendedor?>(null) }

    // Efectos
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.PRODUCTS)
        viewModel.loadEmprendedores()
    }

    LaunchedEffect(stateEmprendedor.isLoading) {
        if (!stateEmprendedor.isLoading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(stateEmprendedor.notification) {
        stateEmprendedor.notification.takeIf { it.isVisible }?.let { notification ->
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
                    title = "Emprendedores",
                    isSearchVisible = isSearchVisible,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
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
                // Contenido principal
                PullToRefreshComponent(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.loadEmprendedores()
                    }
                ) {
                    EmprendedoresListContent(
                        stateEmprendedor = stateEmprendedor,
                        lazyListState = lazyListState,
                        onEmprendedorClick = { emprendedor ->
                            selectedEmprendedor = emprendedor
                        }
                    )
                }

                // Loading overlay
                if (stateEmprendedor.isLoading && stateEmprendedor.items.isEmpty()) {
                    LoadingOverlay()
                }

                // Detalle del emprendedor (modal)
                selectedEmprendedor?.let { emprendedor ->
                    EmprendedorDetailModal(
                        emprendedor = emprendedor,
                        onDismiss = { selectedEmprendedor = null },
                        onContactClick = {
                            // Abrir contacto con el emprendedor
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmprendedoresListContent(
    stateEmprendedor: EmprendedorState,
    lazyListState: LazyListState,
    onEmprendedorClick: (Emprendedor) -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Estadísticas
        item {
            EmprendedoresStatsCard(
                totalEmprendedores = stateEmprendedor.totalElements,
                isLoading = stateEmprendedor.isLoading
            )
        }

        // Filtros
        item {
            EmprendedoresFilterSection()
        }

        // Lista de emprendedores
        items(stateEmprendedor.items) { emprendedor ->
            EmprendedorCard(
                emprendedor = emprendedor,
                onClick = { onEmprendedorClick(emprendedor) }
            )
        }

        // Espaciado final
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun EmprendedoresFilterSection() {
    var selectedFilter by remember { mutableStateOf("Todos") }
    val filters = listOf("Todos", "Hospedaje", "Artesanías", "Tours", "Gastronomía")

    Column {
        Text(
            text = "Filtrar por categoría:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ScrollableRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    label = filter,
                    selected = filter == selectedFilter,
                    onSelected = { selectedFilter = filter }
                )
            }
        }
    }
}

@Composable
private fun ScrollableRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSelected),
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando emprendedores...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorDetailModal(
    emprendedor: Emprendedor,
    onDismiss: () -> Unit,
    onContactClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emprendedor.razonSocial ?: "Emprendedor",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (emprendedor.imagenes.isNotEmpty()) {
                EmprendedorImagesCarousel(imagenes = emprendedor.imagenes)
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Información básica
            EmprendedorBasicInfo(emprendedor)
            Spacer(modifier = Modifier.height(16.dp))

            // Productos/Servicios
            Text(
                text = "Productos y Servicios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            emprendedor.products.forEach { product ->
                ProductItem(product = product)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Botón de contacto
            Button(
                onClick = onContactClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Contactar al emprendedor",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EmprendedorImagesCarousel(imagenes: List<Imagen>) {
    val pagerState = rememberPagerState(pageCount = { imagenes.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->

            val url = imagenes[page].url_image ?: ""

            AsyncImage(
                model = url,
                contentDescription = "Imagen ${page + 1} del emprendedor",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(imagenes.size) { index ->
                val color = if (pagerState.currentPage == index) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}


@Composable
private fun EmprendedorBasicInfo(emprendedor: Emprendedor) {
    Column {
        // Ubicación
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = emprendedor.lugar ?: emprendedor.address ?: "Ubicación no disponible",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Asociación
        emprendedor.nombre_asociacion?.let { asociacion ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Asociación: $asociacion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // RUC
        emprendedor.ruc?.let { ruc ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RUC: $ruc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Descripción
        emprendedor.description?.let { description ->
            Text(
                text = "Descripción:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProductItem(product: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                product.service_name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "S/. ${product.costo}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            product.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Disponibles: ${product.cantidad}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Text(
                    text = "S/. ${product.costoUnidad} por unidad",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            product.service_description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun EmprendedoresStatsCard(
    totalEmprendedores: Int,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Emprendedores",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isLoading) "Cargando..." else "$totalEmprendedores registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmprendedorCard(
    emprendedor: Emprendedor,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header con imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Imagen de fondo
                val logoUrl = emprendedor.img_logo

                if (!logoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = logoUrl,  // 🔥 usamos directamente el valor de la propiedad
                        contentDescription = "Logo ${emprendedor.razonSocial}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (emprendedor.imagenes.isNotEmpty()) {
                    AsyncImage(
                        model = emprendedor.imagenes.first().url_image,
                        contentDescription = "Imagen del emprendedor",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    DefaultBackgroundGradient()
                }


                // Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = emprendedor.razonSocial ?: "Emprendedor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        emprendedor.lugar?.let { lugar ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = lugar,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        EmprendedorStatusBadge(emprendedor.status)
                    }
                }
            }

            // Contenido inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val productTypes = emprendedor.products
                    .mapNotNull { it.service_name }
                    .distinct()
                    .joinToString(", ")

                if (productTypes.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = productTypes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                emprendedor.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${emprendedor.products.size} productos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    TextButton(
                        onClick = onClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Ver detalles",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun DefaultBackgroundGradient() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun EmprendedorStatusBadge(status: Int) {
    val (statusColor, statusText) = when (status) {
        1 -> Color(0xFF4CAF50) to "Activo" // Verde
        0 -> Color(0xFFF44336) to "Inactivo" // Rojo
        else -> Color(0xFFFF9800) to "Pendiente" // Naranja
    }

    Surface(
        color = statusColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}