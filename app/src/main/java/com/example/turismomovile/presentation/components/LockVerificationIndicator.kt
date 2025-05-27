package com.example.turismomovile.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay


@Composable
fun ShowLoadingDialog(isLoading: Boolean) {
    // Controlamos la animación de escala
    val scaleAnim by animateFloatAsState(
        targetValue = if (isLoading) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing), // Aumento de duración para suavizar la animación
            repeatMode = RepeatMode.Restart
        )
    )

    // Variable que controla cuánto tiempo permanece visible el indicador
    var showDialog by remember { mutableStateOf(isLoading) }

    // Controlamos el tiempo de visibilidad del diálogo de carga
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(40000) // 30 segundos de visibilidad
            showDialog = false // Desaparece después del tiempo
        }
    }


    if (showDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)), // Fondo oscuro con transparencia
            contentAlignment = Alignment.Center
        ) {
            // Caja que contiene el contenido de la carga
            Card(
                modifier = Modifier
                    .width(300.dp)  // Ancho ajustado
                    .height(230.dp) // Alto ajustado
                    .padding(16.dp), // Padding alrededor del contenido
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp), // Padding interno más pequeño
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Espaciado más compacto
                ) {
                    // Círculo de carga con candado en el centro
                    Box(
                        modifier = Modifier
                            .size(50.dp)  // Tamaño ajustado del círculo
                            .align(Alignment.CenterHorizontally)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxSize(), // Ocupa todo el tamaño del box
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp // Grosor de la barra de carga
                        )
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Candado de verificación",
                            modifier = Modifier
                                .size(25.dp)  // Tamaño del ícono del candado
                                .align(Alignment.Center), // Centrado del candado sobre el círculo
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Texto de verificación
                    Text(
                        text = "Verificando credenciales...",
                        style = TextStyle(
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,  // Ajuste de tamaño del texto
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    // Texto de espera
                    Text(
                        text = "Por favor espere...",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Opacidad ajustada
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

