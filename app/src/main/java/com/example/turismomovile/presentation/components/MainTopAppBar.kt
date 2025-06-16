package com.example.turismomovile.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turismomovile.presentation.theme.LocalAppDimens

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
    val dimens = LocalAppDimens.current
    val colors = MaterialTheme.colorScheme

    // Animaciones para las transiciones
    val searchScale by animateFloatAsState(
        targetValue = if (isSearchVisible) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "searchScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        shadowElevation = if (isSearchVisible) 8.dp else 4.dp,
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colors.surface,
                            colors.surfaceVariant.copy(alpha = 0.3f),
                            colors.surface
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AnimatedVisibility(
                visible = isSearchVisible,
                enter = expandHorizontally(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ),
                exit = shrinkHorizontally(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ),
                modifier = Modifier.scale(searchScale)
            ) {
                SearchBarComponent(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    onClose = onCloseSearch
                )
            }

            AnimatedVisibility(
                visible = !isSearchVisible,
                enter = expandHorizontally(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ),
                exit = shrinkHorizontally(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            ) {
                TopAppBarContent(
                    title = title,
                    onClickExplorer = onClickExplorer,
                    onToggleSearch = onToggleSearch,
                    onToggleTheme = onToggleTheme,
                    onStartClick = onStartClick,
                    isDarkMode = isDarkMode,
                    colors = colors,
                    dimens = dimens
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
    colors: ColorScheme,
    dimens: Any
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo/Explorer Button
        AnimatedIconButton(
            onClick = onClickExplorer,
            icon = Icons.Default.Explore,
            contentDescription = "Explorar",
            tint = colors.primary,
            backgroundColor = colors.primaryContainer.copy(alpha = 0.3f)
        )

        // Title with gradient effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = colors.onSurface,
                maxLines = 1
            )
        }

        // Action buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedIconButton(
                onClick = onToggleSearch,
                icon = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = colors.onSurfaceVariant
            )

            AnimatedIconButton(
                onClick = onToggleTheme,
                icon = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Cambiar tema",
                tint = colors.onSurfaceVariant
            )

            PremiumButton(
                onClick = onStartClick,
                colors = colors
            )
        }
    }
}

@Composable
private fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Buscar destinos, eventos...",
                    color = colors.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(
                        onClick = { onQueryChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline.copy(alpha = 0.5f),
                focusedContainerColor = colors.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = colors.surface
            ),
            modifier = Modifier.weight(1f)
        )

        AnimatedIconButton(
            onClick = onClose,
            icon = Icons.Default.Close,
            contentDescription = "Cerrar búsqueda",
            tint = colors.onSurfaceVariant
        )
    }
}

@Composable
private fun AnimatedIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: androidx.compose.ui.graphics.Color,
    backgroundColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Surface(
        onClick = {
            isPressed = true
            onClick()
        },
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier
            .size(44.dp)
            .scale(scale)
            .clip(CircleShape)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun PremiumButton(
    onClick: () -> Unit,
    colors: ColorScheme
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "premiumButtonScale"
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
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        modifier = Modifier
            .scale(scale)
            .height(36.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Ingresar",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                fontSize = 13.sp
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}