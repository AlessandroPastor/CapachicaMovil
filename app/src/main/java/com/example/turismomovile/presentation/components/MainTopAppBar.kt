package com.example.turismomovile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    title: String,
    isSearchVisible: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onToggleSearch: () -> Unit,
    onCloseSearch: () -> Unit,
    onClickExplorer: () -> Unit,
    onStartClick: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search Bar
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    onClose = onCloseSearch,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Regular App Bar Content
            AnimatedVisibility(
                visible = !isSearchVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBarContent(
                    title = title,
                    onClickExplorer = onClickExplorer,
                    onToggleSearch = onToggleSearch,
                    onToggleTheme = onToggleTheme,
                    onStartClick = onStartClick,
                    isDarkMode = isDarkMode,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun TopAppBarContent(
    title: String,
    onClickExplorer: () -> Unit,
    onToggleSearch: () -> Unit,
    onToggleTheme: () -> Unit,
    onStartClick: () -> Unit,
    isDarkMode: Boolean,
    colors: ColorScheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Explorer Button
        IconButton(
            onClick = onClickExplorer,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Explorar",
                tint = colors.primary
            )
        }

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = colors.onSurface,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        // Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Search Button
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = colors.onSurfaceVariant
                )
            }

            // Theme Toggle Button
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Cambiar tema",
                    tint = colors.onSurfaceVariant
                )
            }

            // Premium Button
            PremiumButton(
                onClick = onStartClick,
                colors = colors
            )
        }
    }
}

@Composable
private fun PremiumButton(
    onClick: () -> Unit,
    colors: ColorScheme
) {
    var isPressed by remember { mutableStateOf(false) }

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 4.dp,
        animationSpec = tween(durationMillis = 200),
        label = "buttonElevation"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = elevation
        ),
        modifier = Modifier.height(36.dp),
        border = BorderStroke(
            width = 1.dp,
            color = colors.primary.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = "Ingresar",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}