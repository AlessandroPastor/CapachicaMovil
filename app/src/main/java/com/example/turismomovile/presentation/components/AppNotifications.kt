    package com.example.turismomovile.presentation.components

    import androidx.compose.animation.*
    import androidx.compose.animation.core.animateFloatAsState
    import androidx.compose.animation.core.tween
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.isSystemInDarkTheme
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
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.Shape
    import androidx.compose.ui.platform.LocalContext
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
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn() +
                        scaleIn(initialScale = 0.9f),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut() +
                        scaleOut(targetScale = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppNotification(
        message: String,
        type: NotificationType,
        onDismiss: () -> Unit,
        durationMillis: Int = 4000
    ) {
        val coroutineScope = rememberCoroutineScope()
        val isDarkTheme = isSystemInDarkTheme()
        var isVisible by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            delay(durationMillis.toLong())
            isVisible = false
            delay(300)
            onDismiss()
        }

        val progress by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(durationMillis = durationMillis)
        )

        val (icon, containerColor, contentColor, accentColor, shape) = when (type) {
            NotificationType.SUCCESS -> {
                val container = if (isDarkTheme) Color(0xFF2E7D32) else Color(0xFFE8F5E9)
                val content = if (isDarkTheme) Color(0xFFE8F5E9) else Color(0xFF1B5E20)
                Quintuple(Icons.Default.CheckCircle, container, content, Color(0xFF4CAF50),
                    RoundedCornerShape(16.dp))
            }
            NotificationType.ERROR -> {
                val container = if (isDarkTheme) Color(0xFFC62828) else Color(0xFFFFEBEE)
                val content = if (isDarkTheme) Color(0xFFFFEBEE) else Color(0xFFB71C1C)
                Quintuple(Icons.Default.Error, container, content, Color(0xFFEF5350),
                    RoundedCornerShape(16.dp))
            }
            NotificationType.WARNING -> {
                val container = if (isDarkTheme) Color(0xFFF9A825) else Color(0xFFFFF8E1)
                val content = if (isDarkTheme) Color(0xFFFFF8E1) else Color(0xFFF57F17)
                Quintuple(Icons.Default.Warning, container, content, Color(0xFFFFC107),
                    RoundedCornerShape(16.dp))
            }
            NotificationType.INFO -> {
                val container = if (isDarkTheme) Color(0xFF0277BD) else Color(0xFFE1F5FE)
                val content = if (isDarkTheme) Color(0xFFE1F5FE) else Color(0xFF01579B)
                Quintuple(Icons.Default.Info, container, content, Color(0xFF29B6F6),
                    RoundedCornerShape(16.dp))
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically { -it } + fadeIn() + scaleIn(),
            exit = slideOutVertically { -it } + fadeOut() + scaleOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = shape,
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = type.name,
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            accentColor.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                                .padding(4.dp),
                            tint = accentColor
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (type) {
                                    NotificationType.SUCCESS -> "Éxito"
                                    NotificationType.ERROR -> "Error"
                                    NotificationType.WARNING -> "Advertencia"
                                    NotificationType.INFO -> "Información"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = contentColor
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp
                                ),
                                color = contentColor.copy(alpha = 0.9f)
                            )
                        }

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
                                contentDescription = "Cerrar",
                                modifier = Modifier.size(20.dp),
                                tint = contentColor.copy(alpha = 0.8f)
                            )
                        }

                    }

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                        color = accentColor,
                        trackColor = accentColor.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }

    private data class Quintuple<out A, out B, out C, out D, out E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )