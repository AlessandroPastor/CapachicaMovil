package com.example.turismomovile.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val duration: Long = 3000L,
    val title: String? = null,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

@Composable
fun rememberNotificationState(): MutableState<NotificationState> {
    return remember { mutableStateOf(NotificationState()) }
}

fun MutableState<NotificationState>.showNotification(
    message: String,
    type: NotificationType = NotificationType.SUCCESS,
    duration: Long = 3000L,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    value = NotificationState(
        message = message,
        type = type,
        isVisible = true,
        duration = duration,
        title = title,
        actionLabel = actionLabel,
        onAction = onAction
    )
}

@Composable
fun NotificationHost(
    state: MutableState<NotificationState>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    Box(modifier = modifier.fillMaxSize()) {
        content()

        val currentState = state.value

        LaunchedEffect(currentState) {
            if (currentState.isVisible) {
                // Haptic feedback cuando aparece la notificación
                when (currentState.type) {
                    NotificationType.SUCCESS -> hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    NotificationType.ERROR -> hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    else -> hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                delay(currentState.duration)
                state.value = state.value.copy(isVisible = false)
            }
        }

        AnimatedVisibility(
            visible = currentState.isVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(300)) +
                    scaleIn(initialScale = 0.85f, animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(250)
            ) + fadeOut(animationSpec = tween(250)) +
                    scaleOut(targetScale = 0.95f, animationSpec = tween(250)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .imePadding()
                .systemBarsPadding()
        ) {
            ModernNotification(
                message = currentState.message,
                type = currentState.type,
                title = currentState.title,
                actionLabel = currentState.actionLabel,
                onAction = currentState.onAction,
                onDismiss = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    state.value = state.value.copy(isVisible = false)
                }
            )
        }
    }
}

@Composable
private fun ModernNotification(
    message: String,
    type: NotificationType,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    durationMillis: Int = 4000
) {
    val coroutineScope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }

    LaunchedEffect(isPaused) {
        if (!isPaused) {
            delay(durationMillis.toLong())
            isVisible = false
            delay(300)
            onDismiss()
        }
    }

    val progress by animateFloatAsState(
        targetValue = if (isVisible && !isPaused) 0f else 1f,
        animationSpec = if (isPaused) snap() else tween(durationMillis = durationMillis),
        label = "progress"
    )

    val notificationConfig = getNotificationConfig(type)

    // Animación de pulso para el ícono
    val pulseScale by rememberInfiniteTransition("pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clickable(
                onClick = { isPaused = !isPaused }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = notificationConfig.containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            // Fondo con gradiente sutil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                notificationConfig.containerColor,
                                notificationConfig.containerColor.copy(alpha = 0.8f),
                                notificationConfig.containerColor
                            )
                        )
                    )
            )

            Column {
                Row(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Ícono con animación y fondo
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(if (type == NotificationType.SUCCESS) pulseScale else 1f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        notificationConfig.accentColor.copy(alpha = 0.2f),
                                        notificationConfig.accentColor.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = notificationConfig.icon,
                            contentDescription = type.name,
                            modifier = Modifier.size(28.dp),
                            tint = notificationConfig.accentColor
                        )
                    }

                    // Contenido del texto
                    Column(modifier = Modifier.weight(1f)) {
                        // Título (si existe) o tipo por defecto
                        Text(
                            text = title ?: notificationConfig.defaultTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = notificationConfig.contentColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = notificationConfig.contentColor.copy(alpha = 0.9f)
                        )
                    }

                    // Botón de acción (si existe)
                    if (actionLabel != null && onAction != null) {
                        TextButton(
                            onClick = {
                                onAction()
                                onDismiss()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = notificationConfig.accentColor
                            )
                        ) {
                            Text(
                                text = actionLabel,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Botón de cerrar
                    IconButton(
                        onClick = {
                            isVisible = false
                            coroutineScope.launch {
                                delay(300)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar notificación",
                            modifier = Modifier.size(18.dp),
                            tint = notificationConfig.contentColor.copy(alpha = 0.7f)
                        )
                    }
                }

                // Barra de progreso mejorada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            color = notificationConfig.accentColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f - progress)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        notificationConfig.accentColor,
                                        notificationConfig.accentColor.copy(alpha = 0.8f)
                                    )
                                ),
                                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                            )
                    )
                }
            }
        }
    }
}

private data class NotificationConfig(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color,
    val accentColor: Color,
    val defaultTitle: String
)

@Composable
private fun getNotificationConfig(type: NotificationType): NotificationConfig {
    return when (type) {
        NotificationType.SUCCESS -> NotificationConfig(
            icon = Icons.Default.CheckCircle,
            containerColor = Color(0xFFE8F7ED),
            contentColor = Color(0xFF1B5E20),
            accentColor = Color(0xFF2E7D32),
            defaultTitle = "¡Éxito!"
        )
        NotificationType.ERROR -> NotificationConfig(
            icon = Icons.Default.ErrorOutline,
            containerColor = Color(0xFFFFEBEE),
            contentColor = Color(0xFFB71C1C),
            accentColor = Color(0xFFD32F2F),
            defaultTitle = "Error"
        )
        NotificationType.WARNING -> NotificationConfig(
            icon = Icons.Default.Warning,
            containerColor = Color(0xFFFFF8E1),
            contentColor = Color(0xFFE65100),
            accentColor = Color(0xFFFF9800),
            defaultTitle = "Advertencia"
        )
        NotificationType.INFO -> NotificationConfig(
            icon = Icons.Default.Info,
            containerColor = Color(0xFFE3F2FD),
            contentColor = Color(0xFF0D47A1),
            accentColor = Color(0xFF1976D2),
            defaultTitle = "Información"
        )
    }
}


// Extensiones de utilidad para usar fácilmente
@Composable
fun MutableState<NotificationState>.showSuccess(
    message: String,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) = showNotification(message, NotificationType.SUCCESS, 3000L, title, actionLabel, onAction)

@Composable
fun MutableState<NotificationState>.showError(
    message: String,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) = showNotification(message, NotificationType.ERROR, 4000L, title, actionLabel, onAction)

@Composable
fun MutableState<NotificationState>.showWarning(
    message: String,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) = showNotification(message, NotificationType.WARNING, 3500L, title, actionLabel, onAction)

@Composable
fun MutableState<NotificationState>.showInfo(
    message: String,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) = showNotification(message, NotificationType.INFO, 3000L, title, actionLabel, onAction)