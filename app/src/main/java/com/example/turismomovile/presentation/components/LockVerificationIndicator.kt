package com.example.turismomovile.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turismomovile.presentation.theme.LocalAppDimens
import kotlinx.coroutines.delay


@Composable
fun ShowLoadingDialog(isLoading: Boolean) {
    val dimens = LocalAppDimens.current

    // Animación elegante de escala
    val scaleAnim by animateFloatAsState(
        targetValue = if (isLoading) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnimation"
    )

    var showDialog by remember { mutableStateOf(isLoading) }

    // Ocultar después de un tiempo determinado
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(30_000)
            showDialog = false
        }
    }

    if (showDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .scale(scaleAnim)
                    .width(280.dp)
                    .height(220.dp)
                    .padding(dimens.spacing_16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Candado de verificación",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text = "Verificando credenciales...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Por favor, espere un momento.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


