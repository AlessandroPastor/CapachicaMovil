package com.example.turismomovile.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RectangleShape, clip = true),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomNavItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Inicio",
                    isSelected = currentSection == LangPageViewModel.Sections.HOME,
                    onClick = {
                        onSectionSelected(LangPageViewModel.Sections.HOME)
                        navController.navigate(Routes.LAND_PAGE)
                    }
                )

                BottomNavItem(
                    icon = Icons.Outlined.Build,
                    selectedIcon = Icons.Filled.Build,
                    label = "Servicios",
                    isSelected = currentSection == LangPageViewModel.Sections.SERVICES,
                    onClick = {
                        onSectionSelected(LangPageViewModel.Sections.SERVICES)
                        navController.navigate(Routes.SERVICES)
                    }
                )

                BottomNavItem(
                    icon = Icons.Outlined.LocationOn,
                    selectedIcon = Icons.Filled.LocationOn,
                    label = "Lugares",
                    isSelected = currentSection == LangPageViewModel.Sections.PLACES,
                    onClick = {
                        onSectionSelected(LangPageViewModel.Sections.PLACES)
                        navController.navigate(Routes.PLACES)
                    }
                )

                BottomNavItem(
                    icon = Icons.Outlined.Star,
                    selectedIcon = Icons.Filled.Star,
                    label = "Eventos",
                    isSelected = currentSection == LangPageViewModel.Sections.EVENTS,
                    onClick = {
                        onSectionSelected(LangPageViewModel.Sections.EVENTS)
                        navController.navigate(Routes.EVENTS)
                    }
                )

                BottomNavItem(
                    icon = Icons.Outlined.Favorite,
                    selectedIcon = Icons.Filled.Favorite,
                    label = "Recomendados",
                    isSelected = currentSection == LangPageViewModel.Sections.RECOMMENDATIONS,
                    onClick = {
                        onSectionSelected(LangPageViewModel.Sections.RECOMMENDATIONS)
                        navController.navigate(Routes.RECOMMENDATIONS)
                    }
                )

                BottomNavItem(
                    icon = Icons.Outlined.ShoppingCart,
                    selectedIcon = Icons.Filled.ShoppingCart,
                    label = "Productos",
                    isSelected = currentSection == LangPageViewModel.Sections.PRODUCTS,
                    onClick = {
                        onSectionSelected(LangPageViewModel.Sections.PRODUCTS)
                        navController.navigate(Routes.PRODUCTS)
                    }
                )
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun BottomNavItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "selectionAnimation"
    )

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 24.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .graphicsLayer {
                alpha = if (isSelected) 1f else 0.8f
                scaleX = 1f + animatedProgress * 0.1f
                scaleY = 1f + animatedProgress * 0.1f
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    } else {
                        Color.Transparent
                    },
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else icon,
                contentDescription = label,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
        }

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
