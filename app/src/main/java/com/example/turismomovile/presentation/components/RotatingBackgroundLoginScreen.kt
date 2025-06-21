package com.example.turismomovile.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun RotatingBackgroundLoginScreen(
    @DrawableRes images: List<Int>,
    transitionDuration: Int = 5000, // 5 segundos por defecto
    crossFadeDuration: Int = 1000, // 1 segundo de transición
    initialIndex: Int = 0,
    contentScale: ContentScale = ContentScale.Crop // Opciones: Crop, FillBounds, Fit, Inside
) {
    require(images.isNotEmpty()) { "Debe proporcionar al menos una imagen" }

    var currentImageIndex by remember { mutableIntStateOf(initialIndex.coerceIn(0, images.lastIndex)) }
    var nextImageIndex by remember { mutableIntStateOf((initialIndex + 1) % images.size) }
    var transitionProgress by remember { mutableFloatStateOf(0f) }

    // Efecto para manejar la rotación de imágenes
    LaunchedEffect(Unit) {
        while (true) {
            delay((transitionDuration - crossFadeDuration).toLong())

            // Iniciar transición
            transitionProgress = 0f
            nextImageIndex = (currentImageIndex + 1) % images.size

            // Animación de crossfade
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = crossFadeDuration,
                    easing = LinearEasing
                )
            ) { value, _ ->
                transitionProgress = value
            }

            // Actualizar imagen actual al finalizar la transición
            currentImageIndex = nextImageIndex
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Imagen actual (se desvanece)
        Image(
            painter = painterResource(id = images[currentImageIndex]),
            contentDescription = "Imagen de fondo actual",
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 1f - transitionProgress
                }
        )

        // Imagen siguiente (aparece)
        Image(
            painter = painterResource(id = images[nextImageIndex]),
            contentDescription = "Imagen de fondo siguiente",
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = transitionProgress
                }
        )
    }
}