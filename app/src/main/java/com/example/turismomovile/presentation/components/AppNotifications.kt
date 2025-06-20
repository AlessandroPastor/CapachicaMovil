package com.example.turismomovile.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.turismomovile.presentation.theme.AppColors
import kotlinx.coroutines.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

enum class NotificationType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

data class NotificationState(
    val message: String = "",
    val type: NotificationType = NotificationType.INFO,
    val isVisible: Boolean = false,
    val duration: Long = 3000L
)

@Composable
fun rememberNotificationState(): MutableState<NotificationState> {
    return remember { mutableStateOf(NotificationState()) }
}

fun MutableState<NotificationState>.showNotification(
    message: String,
    type: NotificationType = NotificationType.SUCCESS,
    duration: Long = 3000L
) {
    value = NotificationState(
        message = message,
        type = type,
        isVisible = true,
        duration = duration
    )
}

@Composable
fun NotificationHost(
    state: MutableState<NotificationState>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = modifier.fillMaxSize()) {
        content()

        val currentState = state.value

        LaunchedEffect(currentState) {
            if (currentState.isVisible) {
                delay(currentState.duration)
                state.value = state.value.copy(isVisible = false)
            }
        }

        AnimatedVisibility(
            visible = currentState.isVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp + navigationBarHeight
                )
                .imePadding()
                .systemBarsPadding()
        ) {
            AppNotification(
                message = currentState.message,
                type = currentState.type,
                onDismiss = { state.value = state.value.copy(isVisible = false) }
            )
        }
    }
}

@Composable
private fun AppNotification(
    message: String,
    type: NotificationType,
    onDismiss: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Configuración de estilo basada en el tipo
    val (icon, containerColor, contentColor, iconTint, shape) = when (type) {
        NotificationType.SUCCESS -> {
            val container = if (isDarkTheme) AppColors.SuccessDark else AppColors.SuccessLight
            val content = if (isDarkTheme) AppColors.SuccessTextDark else AppColors.SuccessTextLight
            Quadruple(
                Icons.Default.CheckCircle,
                container,
                content,
                content,
                RoundedCornerShape(12.dp)
            )
        }
        NotificationType.ERROR -> {
            val container = if (isDarkTheme) AppColors.ErrorDark else AppColors.ErrorLight
            val content = if (isDarkTheme) AppColors.ErrorTextDark else AppColors.ErrorTextLight
            Quadruple(
                Icons.Default.Error,
                container,
                content,
                content,
                RoundedCornerShape(12.dp)
            )
        }
        NotificationType.WARNING -> {
            val container = if (isDarkTheme) AppColors.WarningDark else AppColors.WarningLight
            val content = if (isDarkTheme) AppColors.WarningTextDark else AppColors.WarningTextLight
            Quadruple(
                Icons.Default.Warning,
                container,
                content,
                content,
                RoundedCornerShape(12.dp)
            )
        }
        NotificationType.INFO -> {
            val container = if (isDarkTheme) AppColors.InfoDark else AppColors.InfoLight
            val content = if (isDarkTheme) AppColors.InfoTextDark else AppColors.InfoTextLight
            Quadruple(
                Icons.Default.Info,
                container,
                content,
                content,
                RoundedCornerShape(12.dp)
            )
        }
    }

    Surface(
        modifier = Modifier
            .clip(shape as Shape)
            .shadow(
                elevation = 8.dp,
                shape = shape as Shape,
                ambientColor = MaterialTheme.colorScheme.outline,
                spotColor = MaterialTheme.colorScheme.outline
            ),
        color = containerColor,
        contentColor = contentColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con fondo circular
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = type.name,
                    modifier = Modifier.size(18.dp),
                    tint = iconTint
                )
            }

            // Mensaje con tipografía mejorada
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = contentColor
            )

            // Botón de cerrar más sutil
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = contentColor.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Clase helper para manejar múltiples valores
private data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val shape: Any
)