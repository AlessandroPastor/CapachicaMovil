package com.example.turismomovile.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
            icon = Icons.Outlined.Build,
            selectedIcon = Icons.Filled.Build,
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
            icon = Icons.Outlined.Star,
            selectedIcon = Icons.Filled.Star,
            label = "Eventos",
            section = LangPageViewModel.Sections.EVENTS,
            route = Routes.EVENTS
        ),
        BottomNavItem(
            icon = Icons.Outlined.Favorite,
            selectedIcon = Icons.Filled.Favorite,
            label = "Favoritos",
            section = LangPageViewModel.Sections.RECOMMENDATIONS,
            route = Routes.RECOMMENDATIONS
        ),
        BottomNavItem(
            icon = Icons.Outlined.ShoppingCart,
            selectedIcon = Icons.Filled.ShoppingCart,
            label = "Productos",
            section = LangPageViewModel.Sections.PRODUCTS,
            route = Routes.PRODUCTS
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
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

@Composable
private fun BottomNavItemComponent(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        },
        animationSpec = tween(300),
        label = "iconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        },
        animationSpec = tween(300),
        label = "textColor"
    )

    Surface(
        onClick = onClick,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = item.label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                style = MaterialTheme.typography.labelSmall
            )
        }
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