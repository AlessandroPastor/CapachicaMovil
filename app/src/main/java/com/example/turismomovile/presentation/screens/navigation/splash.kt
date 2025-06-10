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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turismomovile.R
import com.example.turismomovile.presentation.theme.LocalAppDimens
import kotlinx.coroutines.delay

private const val SPLASH_ANIMATION_DURATION = 800
private const val SPLASH_TOTAL_DURATION = 2500L
private const val INITIAL_DELAY = 300L

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val dimens = LocalAppDimens.current

    // Estados para las animaciones
    var isVisible by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    val progress by rememberProgressAnimation(isVisible = showProgress)

    // Animaciones principales
    val animations = rememberSplashAnimations(isVisible = isVisible)

    // Efecto principal del splash
    LaunchedEffect(Unit) {
        delay(INITIAL_DELAY)
        isVisible = true
        delay(1000L)
        showProgress = true
        delay(SPLASH_TOTAL_DURATION - INITIAL_DELAY - 1000L)
        onSplashFinished()
    }

    SplashContent(
        modifier = modifier,
        backgroundGradient = createBackgroundGradient(colorScheme),
        logoScale = animations.logoScale,
        textScale = animations.textScale,
        fadeAlpha = animations.fadeAlpha,
        progress = progress,
        colorScheme = colorScheme,
        dimens = dimens
    )
}

@Composable
private fun SplashContent(
    modifier: Modifier,
    backgroundGradient: Brush,
    logoScale: Float,
    textScale: Float,
    fadeAlpha: Float,
    progress: Float,
    colorScheme: androidx.compose.material3.ColorScheme,
    dimens: com.example.turismomovile.presentation.theme.AppDimensions
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = dimens.spacing_32.dp)
        ) {
            // Logo animado con sombra
            AnimatedLogo(
                scale = logoScale,
                alpha = fadeAlpha,
                modifier = Modifier.padding(bottom = dimens.spacing_32.dp)
            )

            // Contenido de texto
            AnimatedTextContent(
                scale = textScale,
                alpha = fadeAlpha,
                colorScheme = colorScheme,
                dimens = dimens
            )

            Spacer(modifier = Modifier.height(dimens.spacing_48.dp))

            // Indicador de progreso
            AnimatedProgressIndicator(
                progress = progress,
                colorScheme = colorScheme,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
private fun AnimatedLogo(
    scale: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(160.dp)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Image(
            painter = painterResource(R.drawable.capachica),
            contentDescription = "Logo Capachica Turismo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            alpha = alpha
        )
    }
}

@Composable
private fun AnimatedTextContent(
    scale: Float,
    alpha: Float,
    colorScheme: androidx.compose.material3.ColorScheme,
    dimens: com.example.turismomovile.presentation.theme.AppDimensions
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Text(
            text = "Capachica Turismo",
            color = colorScheme.onPrimary.copy(alpha = alpha),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(dimens.spacing_8.dp))

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    colorScheme.onPrimary.copy(alpha = 0.1f),
                    RoundedCornerShape(16.dp)
                ),
            color = Color.Transparent
        ) {
            Text(
                text = "Gestión 2023 - 2026",
                color = colorScheme.onPrimary.copy(alpha = alpha * 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = dimens.spacing_16.dp,
                    vertical = dimens.spacing_8.dp
                )
            )
        }

        Spacer(modifier = Modifier.height(dimens.spacing_16.dp))

        Text(
            text = "Descubre la belleza natural",
            color = colorScheme.onPrimary.copy(alpha = alpha * 0.8f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun AnimatedProgressIndicator(
    progress: Float,
    colorScheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = colorScheme.onPrimary,
        trackColor = colorScheme.onPrimary.copy(alpha = 0.3f),
        strokeCap = StrokeCap.Round
    )
}

// Composable para manejar las animaciones principales
@Composable
private fun rememberSplashAnimations(isVisible: Boolean): SplashAnimations {
    val logoScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScaleAnimation"
    )

    val textScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = tween(
            durationMillis = SPLASH_ANIMATION_DURATION,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "textScaleAnimation"
    )

    val fadeAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = SPLASH_ANIMATION_DURATION,
            easing = LinearEasing
        ),
        label = "fadeAnimation"
    )

    return remember(logoScale, textScale, fadeAlpha) {
        SplashAnimations(
            logoScale = logoScale,
            textScale = textScale,
            fadeAlpha = fadeAlpha
        )
    }
}

// Composable para la animación del progreso
@Composable
private fun rememberProgressAnimation(isVisible: Boolean): State<Float> {
    return animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = LinearEasing
        ),
        label = "progressAnimation"
    )
}

// Función helper para crear el gradiente de fondo
private fun createBackgroundGradient(
    colorScheme: androidx.compose.material3.ColorScheme
): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            colorScheme.primary,
            colorScheme.primary.copy(alpha = 0.8f),
            colorScheme.primaryContainer
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )
}

// Data class para agrupar las animaciones
private data class SplashAnimations(
    val logoScale: Float,
    val textScale: Float,
    val fadeAlpha: Float
)