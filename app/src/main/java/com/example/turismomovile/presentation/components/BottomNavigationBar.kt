package com.example.turismomovile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.turismomovile.presentation.screens.land_page.LangPageViewModel
import io.dev.kmpventas.presentation.navigation.Routes

@Composable
fun BottomNavigationBar(
    currentSection: LangPageViewModel.Sections,
    onSectionSelected: (LangPageViewModel.Sections) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        BottomNavItem(
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home,
            label = "Inicio",
            section = LangPageViewModel.Sections.HOME,
            route = Routes.LAND_PAGE
        ),
        BottomNavItem(
            icon = Icons.Outlined.MiscellaneousServices,
            selectedIcon = Icons.Filled.MiscellaneousServices,
            label = "Servicios",
            section = LangPageViewModel.Sections.SERVICES,
            route = Routes.SERVICES
        ),
        BottomNavItem(
            icon = Icons.Outlined.LocationOn,
            selectedIcon = Icons.Filled.LocationOn,
            label = "Lugares",
            section = LangPageViewModel.Sections.PLACES,
            route = Routes.PLACES
        ),
        BottomNavItem(
            icon = Icons.Outlined.Event,
            selectedIcon = Icons.Filled.Event,
            label = "Eventos",
            section = LangPageViewModel.Sections.EVENTS,
            route = Routes.EVENTS
        ),
        BottomNavItem(
            icon = Icons.Outlined.FavoriteBorder,
            selectedIcon = Icons.Filled.Favorite,
            label = "Favoritos",
            section = LangPageViewModel.Sections.RECOMMENDATIONS,
            route = Routes.RECOMMENDATIONS
        ),
        BottomNavItem(
            icon = Icons.Outlined.ShoppingBag,
            selectedIcon = Icons.Filled.ShoppingBag,
            label = "Productos",
            section = LangPageViewModel.Sections.PRODUCTS,
            route = Routes.PRODUCTS
        )
    )

    // Diseño más limpio y moderno
    val backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Column {
            // Indicador superior minimalista
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(3.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }

            // Items de navegación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    BottomNavItemComponent(
                        item = item,
                        isSelected = currentSection == item.section,
                        onClick = {
                            onSectionSelected(item.section)
                            safeNavigate(navController, item.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItemComponent(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animaciones suaves y sutiles
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
        },
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        label = "iconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        },
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        label = "textColor"
    )

    // Efecto de fondo sutil para item seleccionado
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.1f else 0f,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        label = "backgroundAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = backgroundAlpha),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    radius = 24.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            ) { onClick() }
            .padding(vertical = 8.dp)
    ) {
        // Icono con transición suave
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Texto siempre visible con transición de color
        Text(
            text = item.label,
            color = textColor,
            fontSize = if (isSelected) 11.sp else 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.2.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

data class BottomNavItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val section: LangPageViewModel.Sections,
    val route: String
)

private fun safeNavigate(navController: NavController, route: String) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (currentRoute != route) {
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }
}