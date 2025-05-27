package com.example.turismomovile.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.turismomovile.presentation.theme.AppColors
import kotlinx.coroutines.*

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
                    // Añadimos el padding inferior considerando la barra de navegación
                    bottom = 16.dp + navigationBarHeight
                )
                // Aseguramos que la notificación esté por encima de todo
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

    val icon = when (type) {
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
        NotificationType.ERROR -> Icons.Default.Error
        NotificationType.WARNING -> Icons.Default.Warning
        NotificationType.INFO -> Icons.Default.Info
    }

    val (backgroundColor, contentColor) = when (type) {
        NotificationType.SUCCESS -> if (isDarkTheme) {
            AppColors.SuccessDark to AppColors.SuccessTextDark
        } else {
            AppColors.SuccessLight to AppColors.SuccessTextLight
        }
        NotificationType.ERROR -> if (isDarkTheme) {
            AppColors.ErrorDark to AppColors.ErrorTextDark
        } else {
            AppColors.ErrorLight to AppColors.ErrorTextLight
        }
        NotificationType.WARNING -> if (isDarkTheme) {
            AppColors.WarningDark to AppColors.WarningTextDark
        } else {
            AppColors.WarningLight to AppColors.WarningTextLight
        }
        NotificationType.INFO -> if (isDarkTheme) {
            AppColors.InfoDark to AppColors.InfoTextDark
        } else {
            AppColors.InfoLight to AppColors.InfoTextLight
        }
    }

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = type.name,
                tint = contentColor
            )
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
            IconButton(
                onClick = onDismiss,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = contentColor
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar"
                )
            }
        }
    }
}