package com.example.turismomovile.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.turismomovile.data.remote.dto.MenuItem
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.presentation.screens.dashboard.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    menuItems: List<MenuItem>,
    expandedMenuItems: Set<String>,
    onMenuItemExpand: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    currentRoute: String? = null,
    viewModel: HomeViewModel = koinInject()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isLoading by remember { derivedStateOf { uiState.isLoading } }

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        uiState.user?.let { user ->
            UserProfileSection(user)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp)
        ) {
            menuItems.forEach { menuItem ->
                MenuItemComponent(
                    menuItem = menuItem,
                    currentRoute = currentRoute,
                    isExpanded = expandedMenuItems.contains(menuItem.id),
                    onExpandToggle = { onMenuItemExpand(menuItem.id) },
                    onNavigate = { route ->
                        coroutineScope.launch {
                            viewModel.setLoading(true) // 🔹 Activa el Loader antes de navegar
                            drawerState.close()
                            onNavigate(route)
                            delay(1000) // 🔹 Simulación de carga
                            viewModel.setLoading(false) // 🔹 Desactiva el Loader después de navegar
                        }
                    }
                )
            }
        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        LogoutButton(viewModel, onLogout)

        Spacer(modifier = Modifier.height(8.dp))
    }
    if (isLoading) {
        LoadingOverlay()
    }
}

@Composable
private fun UserProfileSection(user: User) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(vertical = 16.dp)
    ) {
        val isLandscape = maxWidth > 600.dp // 🔹 Si el ancho es mayor a 600dp, consideramos "modo horizontal"

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isLandscape) Arrangement.SpaceBetween else Arrangement.Start
        ) {
            // 🔹 Si es modo horizontal, la info del usuario va primero
            if (isLandscape) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 🔥 Icono del usuario con ajuste adaptativo
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.padding(16.dp).size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 🔹 Si es vertical, la info del usuario se coloca debajo del icono
            if (!isLandscape) {
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}



@Composable
private fun LogoutButton(viewModel: HomeViewModel, onLogout: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                viewModel.logout()  // 🔹 Limpia la sesión
                onLogout()  // 🔹 Realiza cualquier acción adicional después de cerrar sesión
            }
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    "Cerrar Sesión",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingContent = {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            }
        )
    }
}




@Composable
fun LoadingOverlay() {
    var dotCount by remember { mutableStateOf(0) }

    // Animación de puntos en "Cargando..."
    LaunchedEffect(Unit) {
        while (true) {
            dotCount = (dotCount + 1) % 4
            delay(500)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)) // 🔹 Fondo semitransparente
            .blur(7.dp), // 🔹 Agrega un desenfoque suave
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(32.dp) // 🔹 Espaciado interno para que se vea mejor
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp), // 🔹 Loader más grande
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Texto con animación de puntos "Cargando..."
            Text(
                text = "Cargando" + ".".repeat(dotCount),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
private fun MenuItemComponent(
    menuItem: MenuItem,
    currentRoute: String?,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onNavigate: (String) -> Unit,
    level: Int = 0
) {
    val isSelected = currentRoute == menuItem.link
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            isExpanded -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        label = "backgroundColorAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 12).dp, top = 2.dp, bottom = 2.dp, end = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!menuItem.children.isNullOrEmpty()) {
                    onExpandToggle()
                } else if (menuItem.link.isNotEmpty() && menuItem.type == "basic") {
                    onNavigate(menuItem.link)
                }
            },
        color = backgroundColor,
        tonalElevation = if (isSelected) 1.dp else 0.dp
    )
    {
        Column {
            ListItem(
                headlineContent = {
                    menuItem.title?.let {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    }
                },
                leadingContent = {
                    menuItem.title?.let {
                        getIconForTitle(it)?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = menuItem.title,
                                modifier = Modifier.size(22.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                trailingContent = if (!menuItem.children.isNullOrEmpty()) {
                    {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else null
            )

            if (isExpanded && !menuItem.children.isNullOrEmpty()) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        menuItem.children.forEach { childItem ->
                            MenuItemComponent(
                                menuItem = childItem,
                                currentRoute = currentRoute,
                                isExpanded = false,
                                onExpandToggle = {},
                                onNavigate = onNavigate,
                                level = level + 1
                            )
                        }
                    }
                }
            }
        }
    }
}





private fun getIconForTitle(title: String): ImageVector {
    return when (title.lowercase()) {
        // Secciones principales
        "configuración" -> Icons.Default.Settings
        "catálogo" -> Icons.Default.ViewList
        "contabilidad" -> Icons.Default.Calculate
        "clientes" -> Icons.Default.People
        "compras" -> Icons.Default.ShoppingCart
        "ventas" -> Icons.Default.ShoppingBag
        "movimiento de almacén" -> Icons.Default.Warehouse
        "pagos" -> Icons.Default.CreditCard
        "reportes" -> Icons.Default.BarChart

        // Configuración
        "usuarios" -> Icons.Default.Person
        "usuario empresa" -> Icons.Default.Business
        "modulos padres" -> Icons.Default.Dashboard
        "modulos" -> Icons.Default.Extension
        "roles" -> Icons.Default.AdminPanelSettings
        "municipalidad" -> Icons.Default.Domain
        "asociaciones" -> Icons.Default.List

        // Catálogo
        "unidad medida" -> Icons.Default.Straighten
        "categoría" -> Icons.Default.Category
        "productos" -> Icons.Default.Inventory
        "distibucion de productos" -> Icons.Default.LocalShipping

        // Contabilidad
        "tipo de documento" -> Icons.Default.Description
        "tipo de afectacion" -> Icons.Default.Assignment
        "clase cuenta contable" -> Icons.Default.AccountBalance
        "areas" -> Icons.Default.Apartment
        "plan de contable" -> Icons.Default.RequestQuote
        "almacén" -> Icons.Default.Store
        "dinámica contable" -> Icons.Default.Sync

        // Clientes
        "clientes y proveedores"->Icons.Default.Groups
        "tipo de documento" -> Icons.Default.Badge
        "tipo de entidad" -> Icons.Default.CorporateFare
        "servicios" -> Icons.Default.RoomService
        "entidades" -> Icons.Default.BusinessCenter

        // Compras
        "compras" -> Icons.Default.ShoppingBasket
        "proveedores" -> Icons.Default.LocalShipping

        // Ventas
        "ventas" -> Icons.Default.PointOfSale
        "lista de precios" -> Icons.Default.PriceCheck
        "lista de precios detalle" -> Icons.Default.Receipt
        "clientes" -> Icons.Default.People
        "código de barras" -> Icons.Default.QrCodeScanner

        // Movimientos de Almacén
        "movimiento de almacén" -> Icons.Default.Warehouse
        "inventario" -> Icons.Default.Inventory
        "kardex" -> Icons.Default.Assessment

        // Pagos
        "tipo de operación" -> Icons.Default.SwapHoriz
        "tipo de pago" -> Icons.Default.Payments
        "metodos de pago" -> Icons.Default.Payment
        "pagos" -> Icons.Default.Money

        // Reportes
        "garantías" -> Icons.Default.Verified
        "rentabilidad" -> Icons.Default.TrendingUp

        else -> Icons.Default.Circle
    }
}