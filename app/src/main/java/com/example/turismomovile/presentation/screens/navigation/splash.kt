package com.example.turismomovile.presentation.screens.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
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

    // Estados de animación mejorados con transiciones más fluidas
    var animationPhase by remember { mutableIntStateOf(0) }

    // Animación de entrada del logo con efecto de rebote y rotación
    val logoScale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.5f
            1 -> 1.15f  // Overshoot para efecto más dinámico
            else -> 1f
        },
        animationSpec = when (animationPhase) {
            0 -> spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
            else -> tween(durationMillis = 400, easing = FastOutSlowInEasing)
        },
        label = "logoScale"
    )

    // Rotación 3D más pronunciada al inicio
    val logoRotationX by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 45f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoRotationX"
    )

    val logoRotationZ by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else -25f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "logoRotationZ"
    )

    // Efecto de profundidad (perspectiva)
    val logoCameraDistance by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 8f else 16f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoCameraDistance"
    )

    // Animación de opacidad con efecto de fundido más suave
    val logoAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "logoAlpha"
    )

    // Animaciones escalonadas para el texto con efectos de deslizamiento
    val titleTranslationY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 40f,
        animationSpec = tween(durationMillis = 600, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "titleTranslationY"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )

    val subtitleTranslationY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 30f,
        animationSpec = tween(durationMillis = 500, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "subtitleTranslationY"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )

    val sloganTranslationY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 20f,
        animationSpec = tween(durationMillis = 500, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "sloganTranslationY"
    )

    val sloganAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "sloganAlpha"
    )

    // Animación de progreso con efecto de carga más dinámico
    val progress by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    // Efecto de pulsación para el logo cuando termina
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Efecto de brillo intermitente en el logo
    val logoShine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "logoShine"
    )

    // Control de secuencia mejorado
    LaunchedEffect(Unit) {
        delay(INITIAL_DELAY)
        animationPhase = 1  // Mostrar logo y texto con animaciones

        delay(1000L)
        animationPhase = 2  // Iniciar barra de progreso y efectos finales

        delay(1000L)
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
            ) {
                // Efecto de brillo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (animationPhase >= 1) {
                        val radius = size.minDimension * 0.6f * logoShine
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f * logoShine),
                                    Color.Transparent
                                ),
                                radius = radius
                            ),
                            radius = radius,
                            center = center,
                            blendMode = BlendMode.Overlay
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(logoScale * if (animationPhase >= 2) pulseScale else 1f)
                        .graphicsLayer {
                            rotationX = logoRotationX
                            rotationZ = logoRotationZ
                            cameraDistance = logoCameraDistance
                            alpha = logoAlpha
                        }
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            spotColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                            ambientColor = colorScheme.onPrimary.copy(alpha = 0.1f)
                        ),
                    shape = CircleShape,
                    color = Color.Transparent,
                    shadowElevation = 8.dp
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
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Textos con animaciones escalonadas y efectos de deslizamiento
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Capachica Turismo",
                    color = colorScheme.onPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(titleAlpha)
                        .graphicsLayer { translationY = titleTranslationY },
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Gestión 2023 - 2026",
                    color = colorScheme.onPrimary.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .alpha(subtitleAlpha)
                        .graphicsLayer { translationY = subtitleTranslationY },
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descubre la belleza natural",
                    color = colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier
                        .alpha(sloganAlpha)
                        .graphicsLayer { translationY = sloganTranslationY },
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Barra de progreso con animación mejorada
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(220.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(3.dp),
                        spotColor = colorScheme.primary.copy(alpha = 0.3f)
                    ),
                color = colorScheme.onPrimary,
                trackColor = colorScheme.onPrimary.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}