package com.example.turismomovile.presentation.screens.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turismomovile.R
import kotlinx.coroutines.delay




@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    var fadeOut by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), label = "alpha animation"
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "scale animation"
    )

    val imageOffsetY = animateIntAsState(
        targetValue = if (startAnimation) 0 else -400,
        animationSpec = tween(durationMillis = 1600, easing = EaseOutBounce),
        label = "image slide in"
    )

    val glowAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glow animation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3500)
        fadeOut = true
        delay(500)
        onSplashFinished()
    }

    AnimatedVisibility(
        visible = !fadeOut,
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                AnimatedVisibility(
                    visible = startAnimation,
                    enter = slideInVertically(initialOffsetY = { -it })
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
                            .shadow(10.dp, shape = CircleShape)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.capachica),
                            contentDescription = "Logo Capachica",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                                .graphicsLayer(
                                    scaleX = glowAnim.value,
                                    scaleY = glowAnim.value
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
                AnimatedText("Capachica Turismo", 36.sp, FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                AnimatedText("Gestion 2023 - 2026", 20.sp, FontWeight.Medium)

                Spacer(modifier = Modifier.height(40.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
fun AnimatedText(text: String, fontSize: TextUnit, fontWeight: FontWeight) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)) + slideInVertically { it / 3 }
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}
