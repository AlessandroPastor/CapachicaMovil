package com.example.turismomovile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon


@Composable
fun WhatsAppFloatingButton(
    phoneNumber: String = "+51963378995", // Número de ejemplo para Perú
    message: String = "¡Hola! Me interesa información sobre servicios turísticos",
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val uriHandler = LocalUriHandler.current
    var isPressed by remember { mutableStateOf(false) }

    // Colores de WhatsApp modernos
    val whatsappGreen = Color(0xFF25D366)
    val whatsappGreenDark = Color(0xFF128C7E)
    val whatsappGreenLight = Color(0xFF34E877)

    // Animaciones suaves
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "shadowElevation"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .size(64.dp)
                .shadow(
                    elevation = shadowElevation,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(whatsappGreenLight, whatsappGreen, whatsappGreenDark),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        radius = 32.dp,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                ) {
                    val url = "https://wa.me/$phoneNumber?text=${message}"
                    uriHandler.openUri(url)
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            // Icono de WhatsApp personalizado (usando Chat como alternativa)
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Contactar por WhatsApp",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun WhatsAppFloatingButtonWithLabel(
    phoneNumber: String = "+51963378995",
    message: String = "¡Hola! Me interesa información sobre servicios turísticos",
    label: String = "¿Necesitas ayuda?",
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    showLabel: Boolean = true
) {
    val uriHandler = LocalUriHandler.current
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    // Colores de WhatsApp
    val whatsappGreen = Color(0xFF25D366)
    val whatsappGreenDark = Color(0xFF128C7E)
    val whatsappGreenLight = Color(0xFF34E877)

    // Animaciones para escala del botón
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    // Contenedor que controla la visibilidad del botón y la etiqueta
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { it }
        ) + fadeOut()
    ) {
        // Fila para posicionar el botón a la izquierda y la etiqueta a la derecha
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start, // Alinea todo a la izquierda
            modifier = modifier.padding(start = 16.dp, bottom = 16.dp) // Ajusta el espaciado
        ) {
            // Botón principal
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        clip = false
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                whatsappGreenLight,
                                whatsappGreen,
                                whatsappGreenDark
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            radius = 32.dp,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    ) {
                        val url = "https://wa.me/$phoneNumber?text=${message}"
                        uriHandler.openUri(url)
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                // Efecto de pulso sutil
                PulseEffect(
                    color = Color.White.copy(alpha = 0.3f),
                    isVisible = true
                )
                Icon(
                    imageVector = Icons.Filled.Whatsapp, // Icono de WhatsApp
                    contentDescription = "Contactar por WhatsApp",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Etiqueta con el mensaje de ayuda (a la derecha del botón)
            AnimatedVisibility(
                visible = showLabel,
                enter = slideInHorizontally(
                    initialOffsetX = { it / 2 }
                ) + fadeIn(),
                exit = slideOutHorizontally(
                    targetOffsetX = { it / 2 }
                ) + fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 8.dp,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.padding(start = 12.dp) // Coloca un espacio entre el botón y el texto
                ) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PulseEffect(
    color: Color,
    isVisible: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    if (isVisible) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha))
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

// Componente específico para turismo con mensajes predefinidos
@Composable
fun TourismWhatsAppButton(
    phoneNumber: String,
    tourType: TourismMessageType = TourismMessageType.GENERAL,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    showLabel: Boolean = true
) {
    val message = when (tourType) {
        TourismMessageType.GENERAL -> "¡Hola! Me interesa información sobre servicios turísticos"
        TourismMessageType.HOTELS -> "¡Hola! Busco información sobre alojamiento y hoteles"
        TourismMessageType.TOURS -> "¡Hola! Me gustaría conocer sobre tours y excursiones disponibles"
        TourismMessageType.TRANSPORT -> "¡Hola! Necesito información sobre transporte turístico"
        TourismMessageType.RESTAURANTS -> "¡Hola! Busco recomendaciones de restaurantes y gastronomía local"
        TourismMessageType.ACTIVITIES -> "¡Hola! Me interesan las actividades y eventos turísticos"
    }

    val label = when (tourType) {
        TourismMessageType.GENERAL -> "¿Necesitas ayuda?"
        TourismMessageType.HOTELS -> "Consulta alojamiento"
        TourismMessageType.TOURS -> "Reserva tu tour"
        TourismMessageType.TRANSPORT -> "Consulta transporte"
        TourismMessageType.RESTAURANTS -> "Encuentra restaurantes"
        TourismMessageType.ACTIVITIES -> "Descubre actividades"
    }

    WhatsAppFloatingButtonWithLabel(
        phoneNumber = phoneNumber,
        message = message,
        label = label,
        modifier = modifier,
        isVisible = isVisible,
        showLabel = showLabel
    )
}

enum class TourismMessageType {
    GENERAL,
    HOTELS,
    TOURS,
    TRANSPORT,
    RESTAURANTS,
    ACTIVITIES
}