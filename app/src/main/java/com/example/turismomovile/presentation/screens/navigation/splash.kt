package com.example.turismomovile.presentation.screens.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.turismomovile.R
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados de animación mejorados
    var animationState by remember { mutableStateOf(AnimationState.Initial) }

    // Animaciones principales
    val logoAlpha by animateFloatAsState(
        targetValue = when (animationState) {
            AnimationState.Initial -> 0f
            else -> 1f
        },
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = when (animationState) {
            AnimationState.Initial -> 0.3f
            AnimationState.LogoEntry -> 1.1f
            AnimationState.LogoSettle -> 1f
            AnimationState.TextEntry -> 1f
            AnimationState.Complete -> 1f
            AnimationState.Exit -> 0.8f
        },
        animationSpec = when (animationState) {
            AnimationState.LogoEntry -> spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
            else -> tween(800, easing = FastOutSlowInEasing)
        },
        label = "logoScale"
    )

    // Animación de rotación suave para el logo
    val logoRotation by rememberInfiniteTransition(label = "logoRotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Efecto shimmer
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    // Gradiente de fondo dinámico
    val gradientAnimation by rememberInfiniteTransition(label = "gradient").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientShift"
    )

    val dynamicGradient = Brush.radialGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
        ),
        center = Offset(
            x = 0.5f + gradientAnimation * 0.3f,
            y = 0.5f + sin(gradientAnimation * 2 * PI).toFloat() * 0.2f
        ),
        radius = 800f + gradientAnimation * 200f
    )

    // Control de secuencia mejorado
    LaunchedEffect(Unit) {
        delay(500)
        animationState = AnimationState.LogoEntry
        delay(1000)
        animationState = AnimationState.LogoSettle
        delay(800)
        animationState = AnimationState.TextEntry
        delay(2000)
        animationState = AnimationState.Complete
        delay(1500)
        animationState = AnimationState.Exit
        delay(800)
        onSplashFinished()
    }

    // Contenido principal
    AnimatedVisibility(
        visible = animationState != AnimationState.Exit,
        enter = fadeIn(tween(800)),
        exit = fadeOut(tween(800)) + scaleOut(tween(800)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(dynamicGradient)
        ) {
            // Sistema de partículas de fondo
            FloatingParticles(
                particleCount = 15,
                isActive = animationState >= AnimationState.LogoSettle
            )

            // Contenido principal centrado
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                // Logo mejorado con múltiples efectos
                EnhancedLogo(
                    scale = logoScale,
                    alpha = logoAlpha,
                    rotation = logoRotation,
                    shimmerOffset = shimmerOffset,
                    isGlowing = animationState >= AnimationState.LogoSettle
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Textos con animaciones escalonadas
                AnimatedContent(
                    targetState = animationState >= AnimationState.TextEntry,
                    transitionSpec = {
                        slideInVertically(
                            animationSpec = tween(1000, easing = FastOutSlowInEasing)
                        ) { it } + fadeIn(tween(1000)) togetherWith
                                slideOutVertically(
                                    animationSpec = tween(500)
                                ) { -it } + fadeOut(tween(500))
                    },
                    label = "textContent"
                ) { showText ->
                    if (showText) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Título principal con efecto shimmer
                            ShimmerText(
                                text = "Capachica Turismo",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                shimmerOffset = shimmerOffset,
                                delay = 0
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Subtítulo
                            AnimatedTextWithDelay(
                                text = "Gestión 2023 - 2026",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                delay = 300
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))

                // Indicador de progreso mejorado
                if (animationState >= AnimationState.Complete) {
                    ModernProgressIndicator()
                }
            }

            // Efectos de overlay en las esquinas
            CornerEffects()
        }
    }
}

@Composable
private fun EnhancedLogo(
    scale: Float,
    alpha: Float,
    rotation: Float,
    shimmerOffset: Float,
    isGlowing: Boolean
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowing) 0.6f else 0f,
        animationSpec = tween(1000),
        label = "glowAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(280.dp)
    ) {
        // Glow effect layers
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((300 + index * 20).dp)
                    .scale(scale * 0.9f)
                    .alpha(glowAlpha * (0.3f - index * 0.08f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Logo principal con efectos
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(scale)
                .alpha(alpha)
                .rotate(rotation * 0.1f) // Rotación muy sutil
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .shadow(
                    elevation = 32.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primary,
                    spotColor = MaterialTheme.colorScheme.secondary
                )
        ) {
            Image(
                painter = painterResource(R.drawable.capachica),
                contentDescription = "Logo Capachica",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .drawWithContent {
                        drawContent()
                        // Efecto shimmer sobre la imagen
                        if (shimmerOffset > -0.5f && shimmerOffset < 0.5f) {
                            val shimmerBrush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                start = Offset(
                                    size.width * (shimmerOffset + 0.5f) - 100f,
                                    0f
                                ),
                                end = Offset(
                                    size.width * (shimmerOffset + 0.5f) + 100f,
                                    size.height
                                )
                            )
                            drawRect(shimmerBrush)
                        }
                    }
            )
        }

        // Anillo decorativo
        if (isGlowing) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .scale(scale)
                    .alpha(alpha * 0.8f)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                Color.Transparent,
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .rotate(rotation * 0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun ShimmerText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    shimmerOffset: Float,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it / 2 }
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .drawWithContent {
                    drawContent()
                    // Efecto shimmer en el texto
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent
                        ),
                        start = Offset(
                            size.width * (shimmerOffset + 1f) / 2f - 100f,
                            0f
                        ),
                        end = Offset(
                            size.width * (shimmerOffset + 1f) / 2f + 100f,
                            size.height
                        )
                    )
                    drawRect(shimmerBrush, blendMode = BlendMode.Plus)
                },
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun AnimatedTextWithDelay(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    color: Color,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it }
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ModernProgressIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "progress")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progressRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        // Círculo de fondo pulsante
        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(pulseScale)
                .alpha(0.3f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
        )

        // Indicador principal
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .rotate(rotation),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 4.dp,
            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun FloatingParticles(
    particleCount: Int,
    isActive: Boolean
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 4f,
                speed = Random.nextFloat() * 0.002f + 0.001f,
                alpha = Random.nextFloat() * 0.6f + 0.2f
            )
        }
    }

    val animatedParticles by rememberUpdatedState(particles)

    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isActive) {
            animatedParticles.forEach { particle ->
                val currentTime = System.currentTimeMillis()
                val animatedY = (particle.y + sin(currentTime * particle.speed) * 0.1f) % 1f
                val animatedX = (particle.x + cos(currentTime * particle.speed * 0.5f) * 0.05f) % 1f

                drawCircle(
                    color = Color.White.copy(alpha = particle.alpha),
                    radius = particle.size,
                    center = Offset(
                        size.width * animatedX,
                        size.height * animatedY
                    )
                )
            }
        }
    }
}

@Composable
private fun CornerEffects() {
    val infiniteTransition = rememberInfiniteTransition(label = "corners")

    val cornerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cornerAlpha"
    )

    // Efectos en las esquinas
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Esquina superior izquierda
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(bottomEnd = 30.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = cornerAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        // Esquina inferior derecha
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(topStart = 40.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = cornerAlpha * 0.7f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

private enum class AnimationState {
    Initial, LogoEntry, LogoSettle, TextEntry, Complete, Exit
}