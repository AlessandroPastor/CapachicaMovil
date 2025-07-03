package com.example.turismomovile.presentation.screens.land_page

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turismomovile.R
import com.example.turismomovile.data.remote.dto.configuracion.Emprendedor
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorState
import com.example.turismomovile.data.remote.dto.configuracion.Imagen
import com.example.turismomovile.data.remote.dto.configuracion.Producto
import com.example.turismomovile.presentation.components.*
import com.example.turismomovile.presentation.screens.land_page.componentsEmprendedor.EmprendedoresStatsCardPremium
import com.example.turismomovile.presentation.screens.land_page.componentsEmprendedor.LogoDeFamilia
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.navigation.Routes

import com.example.turismomovile.presentation.theme.ThemeViewModel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedoresScreen(
    onStartClick: () -> Unit,
    onClickExplorer: () -> Unit,
    navController: NavController,
    viewModel: LangPageViewModel,
    themeViewModel: ThemeViewModel = koinInject()
) {
    // Estados para el LazyColumn y scroll
    val lazyListState = rememberLazyListState()
    var isBottomNavVisible by remember { mutableStateOf(true) }
    val reservaViewModel: ReservaViewModel = koinInject()
    val carrito by reservaViewModel.carrito.collectAsState()
    // Variables para detectar dirección del scroll
    var previousScrollOffset by remember { mutableStateOf(0) }
    var scrollDirection by remember { mutableStateOf(LangPageViewModel.ScrollDirection.NONE) }
    var showCart by remember { mutableStateOf(false) }
    val reservaState by reservaViewModel.state.collectAsState()

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
                lazyListState.firstVisibleItemIndex == 0 && currentScrollOffset < 50 -> true // Mostrar en el top
                scrollDirection == LangPageViewModel.ScrollDirection.UP -> true  // Mostrar al scroll hacia arriba
                scrollDirection == LangPageViewModel.ScrollDirection.DOWN -> false // Ocultar al scroll hacia abajo
                else -> isBottomNavVisible // Mantener estado actual
            }

            previousScrollOffset = currentScrollOffset
        }
    }

    // Estados
    val notificationState = rememberNotificationState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    var isRefreshing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val currentSection by viewModel.currentSection
    var selectedEmprendedor by remember { mutableStateOf<Emprendedor?>(null) }
    val stateEmprendedor by viewModel.stateEmprendedor.collectAsState()
    val sss by viewModel.stateEmprendedor.collectAsState()

    // Paginación
    val currentPage = stateEmprendedor.currentPage
    val totalPages = stateEmprendedor.totalPages

    // Efectos
    LaunchedEffect(Unit) {
        viewModel.onSectionSelected(LangPageViewModel.Sections.PRODUCTS)
        viewModel.loadEmprendedores()

        // Mostrar notificación de bienvenida
        notificationState.showNotification(
            message = "Productos y Servicios de las familias",
            type = NotificationType.SUCCESS,
            duration = 3500 // Duración de la notificación
        )
    }
    // Mostrar notificaciones de las reservas
    LaunchedEffect(reservaState.notification) {
        reservaState.notification.takeIf { it.isVisible }?.let { notification ->
            notificationState.showNotification(
                message = notification.message,
                type = notification.type,
                duration = notification.duration
            )
            reservaViewModel.clearNavigationState()
        }
    }
    // Manejo de estado de carga
    LaunchedEffect(stateEmprendedor.isLoading) {
        if (!stateEmprendedor.isLoading) {
            isRefreshing = false
        }
    }

    // Manejo de notificaciones
    LaunchedEffect(stateEmprendedor.notification) {
        stateEmprendedor.notification.takeIf { it.isVisible }?.let { notification ->
            notificationState.showNotification(
                message = notification.message,
                type = notification.type,
                duration = notification.duration
            )
        }
    }

    // Controlar el estado de refresh con feedback
    LaunchedEffect(stateEmprendedor.isLoading, stateEmprendedor.isLoading) {
        if (!stateEmprendedor.isLoading && !stateEmprendedor.isLoading && isRefreshing) {
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
                        title = "Servicios",
                        isSearchVisible = isSearchVisible,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            viewModel.loadEmprendedores(name = searchQuery)
                        },
                        onToggleSearch = { isSearchVisible = !isSearchVisible },
                        onCloseSearch = {
                            isSearchVisible = false
                            searchQuery = ""
                            viewModel.loadEmprendedores()
                        },
                        onClickExplorer = onClickExplorer,
                        onStartClick = onStartClick,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { themeViewModel.toggleTheme() },
                        searchPlaceholder = "Busca emprendedores"
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
                    // Contenido principal con Pull-to-refresh
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
                            viewModel = viewModel,
                            onEmprendedorClick = { emprendedor ->
                                selectedEmprendedor = emprendedor
                            }
                        )
                    }

                    // Overlay de carga
                    if (stateEmprendedor.isLoading && stateEmprendedor.items.isEmpty()) {
                        LoadingOverlay()
                    }

                    // Modal con detalles del emprendedor
                    selectedEmprendedor?.let { emprendedor ->
                        EmprendedorDetailModal(
                            emprendedor = emprendedor,
                            onDismiss = { selectedEmprendedor = null },
                            onContactClick = {
                                // Lógica para abrir contacto con el emprendedor
                            },
                            onAddToCart = { producto, lugar ->
                                reservaViewModel.agregarAlCarrito(
                                    producto = producto,
                                    cantidad = 1,
                                    lugar = lugar
                                )
                            }
                        )
                    }

                    // Log de depuración visible cada vez que cambia currentPage o totalPages
                    LaunchedEffect(currentPage, totalPages) {
                        println("🧭 [UI] currentPage = $currentPage | totalPages = $totalPages")
                    }

                    // ----- INTEGRACIÓN DEL CARRITO ------
                    // Botón flotante para mostrar el carrito si hay items
                    if (carrito.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 110.dp, end = 12.dp)
                        ) {
                            FloatingActionButton(
                                onClick = { showCart = true },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Ver carrito"
                                )
                            }

                            // Badge/contador chevere
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-8).dp)
                                    .size(24.dp)
                                    .background(
                                        color = Color.Red,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = carrito.sumOf { it.cantidadSeleccionada }.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Modal del carrito
                    if (showCart) {
                        ModalBottomSheet(
                            onDismissRequest = { showCart = false },
                        ) {
                            ShoppingCart(
                                items = carrito,
                                onItemQuantityChange = { item, qty ->
                                    reservaViewModel.actualizarCantidad(item, qty)
                                },
                                onRemoveItem = { item ->
                                    reservaViewModel.quitarDelCarrito(item)
                                },
                                checkoutButton = {
                                    Button(
                                        onClick = {
                                            reservaViewModel.reservarAhora()
                                            showCart = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Reservar")
                                    }
                                }
                            )
                        }
                    }
                    // ----- FIN DE INTEGRACIÓN DEL CARRITO ------
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    println("👈 Anterior presionado | currentPage = $currentPage")
                                    if (currentPage > 0) {
                                        viewModel.previousPage()
                                    }
                                },
                                modifier = Modifier.alpha(if (currentPage > 0) 1f else 0.5f)
                            ) {
                                Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = "Anterior")
                            }

                            Text(
                                text = "Página ${currentPage + 1} de $totalPages",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )

                            FloatingActionButton(
                                onClick = {
                                    println("👉 Siguiente presionado | currentPage = $currentPage")
                                    if (currentPage < totalPages - 1) {
                                        viewModel.nextPage()
                                    }
                                },
                                modifier = Modifier.alpha(if (currentPage < totalPages - 1) 1f else 0.5f)
                            ) {
                                Icon(imageVector = Icons.Default.NavigateNext, contentDescription = "Siguiente")
                            }
                        }
                    }
                    if (reservaState.isDialogOpen && reservaState.lastCreatedReservaCode != null) {
                        AppDialog(
                            title = "Reserva creada",
                            onDismissRequest = { reservaViewModel.dismissSuccessDialog() },
                            confirmButton = {
                                TextButton(onClick = { reservaViewModel.dismissSuccessDialog() }) {
                                    Text("Aceptar")
                                }
                            }
                        ) {
                            Text(
                                "Tu código de reserva es ${reservaState.lastCreatedReservaCode}. " +
                                        "Busca el código en Home y págalo."
                            )
                        }
                    }
                }
            }
        }
    }
}




@Composable
private fun EmprendedoresListContent(
    stateEmprendedor: EmprendedorState,
    lazyListState: LazyListState,
    viewModel: LangPageViewModel,
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
        item {
            EmprendedoresStatsCardPremium(
                totalEmprendedores = stateEmprendedor.totalElements,
                isLoading = stateEmprendedor.isLoading
            )
        }

        item {
            EmprendedoresFilterSection(viewModel)
        }

        items(stateEmprendedor.items) { emprendedor ->
            EmprendedorCard(
                emprendedor = emprendedor,
                onClick = { onEmprendedorClick(emprendedor) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}


@Composable
private fun EmprendedoresFilterSection(
    viewModel: LangPageViewModel
) {
    val selectedFilter by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categories
    val scrollState = rememberScrollState()

    // Colores más vibrantes y alegres
    val chipColors = mapOf(
        "Todos" to Color(0xFF6A1B9A).copy(alpha = 0.2f),  // Púrpura vibrante
        "Hospedaje" to Color(0xFF00C853).copy(alpha = 0.2f),  // Verde esmeralda
        "Artesanías" to Color(0xFFFF4081).copy(alpha = 0.2f),  // Rosa fucsia
        "Turismo" to Color(0xFF2962FF).copy(alpha = 0.2f),  // Azul brillante
        "Gastronomía" to Color(0xFFFF6D00).copy(alpha = 0.2f),  // Naranja intenso
        "Transporte" to Color(0xFF00B8D4).copy(alpha = 0.2f)   // Turquesa
    )

    // Colores para el borde cuando está seleccionado (versiones más saturadas)
    val borderColors = mapOf(
        "Todos" to Color(0xFF6A1B9A),
        "Hospedaje" to Color(0xFF00C853),
        "Artesanías" to Color(0xFFFF4081),
        "Turismo" to Color(0xFF2962FF),
        "Gastronomía" to Color(0xFFFF6D00),
        "Transporte" to Color(0xFF00B8D4)
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Explora por categoría",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedFilter

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = if (isSelected)
                                chipColors[category] ?: MaterialTheme.colorScheme.surface
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected)
                                borderColors[category] ?: MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            viewModel.setCategory(category)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = when (category) {
                                "Todos" -> R.drawable.all
                                "Hospedaje" -> R.drawable.hotel
                                "Artesanías" -> R.drawable.artesania
                                "Turismo" -> R.drawable.torus
                                "Gastronomía" -> R.drawable.gastronia
                                "Transporte" -> R.drawable.velero
                                else -> R.drawable.categoria
                            }),
                            contentDescription = null,
                            tint = if (isSelected)
                                borderColors[category] ?: MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            ),
                            color = if (isSelected)
                                borderColors[category] ?: MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
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
    onContactClick: () -> Unit,
    onAddToCart: (Producto, String?) -> Unit
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
            // Header: Emprendedor y cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = emprendedor.razon_social ?: "Prepended",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = emprendedor.name_family ?: "Nombre de Familia",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carrusel de imágenes si existen
            if (emprendedor.imagenes.isNotEmpty()) {
                EmprendedorImagesCarousel(imagenes = emprendedor.imagenes)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Información básica
            EmprendedorBasicInfo(emprendedor)
            Spacer(modifier = Modifier.height(16.dp))

            // Productos / Servicios
            Text(
                text = "Productos y Servicios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Recorremos los productos y los mostramos
            if (emprendedor.products.isNotEmpty()) {
                emprendedor.products.forEach { product ->
                    ProductItem(
                        product = product,
                        onAddToCart = {
                            onAddToCart(product, emprendedor.lugar)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Text(
                    text = "No hay productos o servicios disponibles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text(
                    text = "Contactar al emprendedor",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
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
private fun ProductItem(
    product: Producto,
    onAddToCart: (Producto) -> Unit
) {
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

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = "Añadir al carrito"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir al carrito")
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
                if (emprendedor.imagenes.isNotEmpty()) {
                    // Mostrar la primera imagen disponible del emprendedor
                    AsyncImage(
                        model = emprendedor.imagenes.first().url_image,
                        contentDescription = "Imagen del emprendedor",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (!emprendedor.img_logo.isNullOrEmpty()) {
                    // Si no hay imágenes, mostrar el logo del emprendedor
                    AsyncImage(
                        model = emprendedor.img_logo,  // Usamos directamente el valor de la propiedad
                        contentDescription = "Logo ${emprendedor.razon_social}",
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
                            text = emprendedor.name_family ?: "Emprendedor",
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
                        LogoDeFamilia(emprendedor.img_logo)
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
                        text = buildAnnotatedString {
                            append("${emprendedor.products.size} servicios")
                            append(" • ")
                            append(emprendedor.products.mapNotNull { it.service_name }.distinct().joinToString(", "))
                        },
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}