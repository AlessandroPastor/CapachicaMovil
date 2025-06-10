package com.example.turismomovile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar lugares, eventos...",
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme

    var isFocused by remember { mutableStateOf(false) }
    val hasContent = query.isNotEmpty()

    val animatedElevation by animateDpAsState(
        targetValue = if (isFocused) 6.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "elevationAnimation"
    )

    SearchBarContainer(
        modifier = modifier,
        elevation = animatedElevation,
        isFocused = isFocused,
        colorScheme = colorScheme
    ) {
        SearchBarContent(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onClose = onClose,
            onFocusChange = { isFocused = it },
            placeholder = placeholder,
            hasContent = hasContent,
            isFocused = isFocused,
            enabled = enabled,
            focusManager = focusManager,
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun SearchBarContainer(
    modifier: Modifier = Modifier,
    elevation: Dp,
    isFocused: Boolean,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = colorScheme.surfaceContainerHigh,
        shadowElevation = elevation,
        border = BorderStroke(
            width = if (isFocused) 1.dp else 0.5.dp,
            color = if (isFocused)
                colorScheme.primary.copy(alpha = 0.5f)
            else
                colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        content()
    }
}

@Composable
private fun SearchBarContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    placeholder: String,
    hasContent: Boolean,
    isFocused: Boolean,
    enabled: Boolean,
    focusManager: FocusManager,
    colorScheme: ColorScheme
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de búsqueda
        SearchIcon(colorScheme = colorScheme)

        Spacer(modifier = Modifier.width(12.dp))

        // Campo de texto
        SearchTextField(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onFocusChange = onFocusChange,
            placeholder = placeholder,
            isFocused = isFocused,
            enabled = enabled,
            focusManager = focusManager,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f)
        )

        // Acciones (limpiar/cancelar)
        SearchActions(
            hasContent = hasContent,
            isFocused = isFocused,
            onClear = {
                onQueryChange("")
                focusManager.clearFocus()
            },
            onCancel = {
                onClose()
                focusManager.clearFocus()
            },
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun SearchIcon(colorScheme: ColorScheme) {
    Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Buscar",
        tint = colorScheme.primary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    placeholder: String,
    isFocused: Boolean,
    enabled: Boolean,
    focusManager: FocusManager,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Placeholder
        if (query.isEmpty() && !isFocused) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )
        }

        // TextField
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                },
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    focusManager.clearFocus()
                }
            ),
            cursorBrush = SolidColor(colorScheme.primary)
        )
    }
}

@Composable
private fun SearchActions(
    hasContent: Boolean,
    isFocused: Boolean,
    onClear: () -> Unit,
    onCancel: () -> Unit,
    colorScheme: ColorScheme
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón limpiar
        AnimatedVisibility(
            visible = hasContent,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            IconButton(
                onClick = onClear,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Limpiar",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Botón cancelar
        AnimatedVisibility(
            visible = isFocused,
            enter = slideInHorizontally(
                initialOffsetX = { it / 2 }
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it / 2 }
            ) + fadeOut()
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary
                )
            }
        }
    }
}
