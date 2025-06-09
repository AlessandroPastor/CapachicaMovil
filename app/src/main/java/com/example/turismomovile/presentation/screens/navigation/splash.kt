package com.example.turismomovile.presentation.screens.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turismomovile.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados de animación
    var animationState by remember { mutableStateOf(AnimationState.Initial) }

    // Animaciones
    val alphaAnim by animateFloatAsState(
        targetValue = if (animationState != AnimationState.Initial) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "alphaAnimation"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = when (animationState) {
            AnimationState.Initial -> 0.9f
            AnimationState.ScaleUp -> 1.05f
            AnimationState.ScaleNormal -> 1f
            AnimationState.Exit -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleAnimation"
    )

    val glowAnim by animateFloatAsState(
        targetValue = if (animationState == AnimationState.ScaleNormal) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1f at 0
                1.08f at 500
                1.1f at 1000
                1.08f at 1500
                1f at 2000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "glowAnimation"
    )

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
        )
    )


    // Control de la secuencia de animaciones
    LaunchedEffect(Unit) {
        delay(300) // Pequeño delay para asegurar la composición inicial
        animationState = AnimationState.ScaleUp
        delay(800)
        animationState = AnimationState.ScaleNormal
        delay(2500)
        animationState = AnimationState.Exit
        delay(500) // Tiempo para la animación de salida
        onSplashFinished()
    }

    // Contenido del splash
    AnimatedVisibility(
        visible = animationState != AnimationState.Exit,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            ) {
                // Logo con efecto de glow
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.secondary,
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
                            .graphicsLayer {
                                scaleX = glowAnim
                                scaleY = glowAnim
                            }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Texto animado
                AnimatedTextComponent(
                    text = "Capachica Turismo",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    delay = 500
                )

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedTextComponent(
                    text = "Gestión 2023 - 2026",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    delay = 800
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Indicador de carga con animación
                AnimatedProgressIndicator(delay = 1200)
            }
        }
    }
}

@Composable
private fun AnimatedTextComponent(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    textColor: Color,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)) +
                slideInVertically(animationSpec = tween(800, easing = FastOutSlowInEasing)) { it / 2 },
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.9f)
        )
    }
}

@Composable
private fun AnimatedProgressIndicator(delay: Long) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "progressAlpha"
    )

    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progressRotation"
    )

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    CircularProgressIndicator(
        modifier = Modifier
            .size(40.dp)
            .rotate(rotation)
            .alpha(alpha),
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
        strokeWidth = 3.dp,
        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
    )
}

private enum class AnimationState {
    Initial, ScaleUp, ScaleNormal, Exit
}