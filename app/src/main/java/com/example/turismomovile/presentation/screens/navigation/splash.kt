package com.example.turismomovile.presentation.screens.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turismomovile.R
import com.example.turismomovile.presentation.theme.LocalAppDimens
import kotlinx.coroutines.delay

private const val SPLASH_TOTAL_DURATION = 2500L
private const val INITIAL_DELAY = 300L

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val dimens = LocalAppDimens.current

    // Estados de animación mejorados
    var animationPhase by remember { mutableIntStateOf(0) }

    // Animaciones más elaboradas
    val logoScale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.7f
            1 -> 1.1f  // Pequeño overshoot para efecto más dinámico
            else -> 1f
        },
        animationSpec = when (animationPhase) {
            0 -> spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
            else -> tween(durationMillis = 500, easing = FastOutSlowInEasing)
        },
        label = "logoScale"
    )

    val logoRotation by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else -15f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoRotation"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing),
        label = "logoAlpha"
    )

    // Animación de texto escalonada
    val titleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )

    val sloganAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "sloganAlpha"
    )

    // Animación de progreso con efecto de carga
    val progress by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 1200
            0.0f at 0 with LinearEasing
            0.3f at 300 with FastOutSlowInEasing
            0.7f at 700 with LinearEasing
            1.0f at 1200 with FastOutSlowInEasing
        },
        label = "progress"
    )

    // Efecto de pulsación para el logo cuando termina
    val pulseScale by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1.05f else 1f,
        animationSpec = repeatable(
            iterations = 2,
            animation = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Control de secuencia mejorado
    LaunchedEffect(Unit) {
        delay(INITIAL_DELAY)
        animationPhase = 1  // Mostrar logo y texto con animaciones

        delay(1200L)
        animationPhase = 2  // Iniciar barra de progreso y efectos finales

        delay(1300L)
        onSplashFinished()  // Finalizar
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.primary,
                        colorScheme.primary.copy(alpha = 0.9f),
                        colorScheme.primaryContainer
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = dimens.spacing_32.dp)
        ) {
            // Logo con animaciones mejoradas
            Surface(
                modifier = Modifier
                    .size(150.dp)
                    .scale(logoScale * pulseScale)
                    .graphicsLayer {
                        rotationZ = logoRotation
                        alpha = logoAlpha
                    }
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = colorScheme.onPrimary.copy(alpha = 0.1f)
                    ),
                shape = CircleShape,
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(R.drawable.capachica),
                    contentDescription = "Logo Capachica",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Textos con animaciones escalonadas
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Capachica Turismo",
                    color = colorScheme.onPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(titleAlpha),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gestión 2023 - 2026",
                    color = colorScheme.onPrimary.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(subtitleAlpha),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descubre la belleza natural",
                    color = colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.alpha(sloganAlpha),
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Barra de progreso con animación mejorada
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(220.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(4.dp),
                        ambientColor = colorScheme.primary
                    ),
                color = colorScheme.onPrimary,
                trackColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

// Extensión para simplificar el alpha
private fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer { this.alpha = alpha }
)