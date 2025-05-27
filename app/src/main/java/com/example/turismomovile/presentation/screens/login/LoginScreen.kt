package com.example.turismomovile.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.turismomovile.R
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.presentation.components.AppButton
import com.example.turismomovile.presentation.components.AppCard
import com.example.turismomovile.presentation.components.AppTextFieldWithKeyboard
import com.example.turismomovile.presentation.components.RotatingBackgroundLoginScreen
import com.example.turismomovile.presentation.components.ShowLoadingDialog
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onBackPressed: () -> Unit, // Agregar el parámetro onBackPressed
// Agregar el NavController
    viewModel: LoginViewModel = koinInject()
) {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val validateEmail = {
        isEmailError = !email.contains("") || !email.contains("")
        !isEmailError
    }

    val validatePassword = {
        isPasswordError = password.length < 3
        !isPasswordError
    }

    val validateAndLogin = {
        if (validateEmail() && validatePassword()) {
            keyboardController?.hide()
            viewModel.login(email, password)
        }
    }

    // Animación de Glow para el logo
    val glowAnim by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glow animation"
    )

    // Animación de entrada del logo
    val logoVisibility = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        logoVisibility.value = true
    }

    AppTheme(darkTheme = isDarkMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (isDarkMode) {
                            listOf(Color(0xFF14213D), Color(0xFF264653))
                        } else {
                            listOf(Color(0xFF0072FF), Color(0xFF00C6FF))
                        },
                        start = Offset(0f, 0f),
                        end = Offset(800f, 1200f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            //Rotación de imágenes de fondo
            val images = listOf(
                R.drawable.fondo,
                R.drawable.fondo,
                R.drawable.fondo2,
            )
            RotatingBackgroundLoginScreen(images = images)

            // Contenido principal (Formulario)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = logoVisibility.value,
                    enter = fadeIn(animationSpec = tween(1000)) + slideInVertically { -150 }
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                            .shadow(10.dp, shape = CircleShape)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.capachica), // Cambia a tu logo
                            contentDescription = "Logo de Capachica Tours",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                                .graphicsLayer(
                                    scaleX = glowAnim,
                                    scaleY = glowAnim
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp), // Bordes redondeados
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
                    elevation = CardDefaults.cardElevation(12.dp) // Añadir una ligera sombra para resaltar el card
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Aumentar el espacio entre los elementos
                    ) {
                        // Título animado
                        AnimatedVisibility(
                            visible = logoVisibility.value,
                            enter = fadeIn(animationSpec = tween(1200))
                        ) {
                            Text(
                                text = "Turismo Capachica",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp, // Aumenta el tamaño del título para mayor visibilidad
                                    letterSpacing = 1.sp, // Espaciado entre letras para un estilo más moderno
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Campos de entrada (Usuario y Contraseña)
                        AppTextFieldWithKeyboard(
                            value = email,
                            onValueChange = {
                                email = it
                                if (isEmailError) validateEmail()
                            },
                            label = "Usuario",
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            isError = isEmailError,
                            errorMessage = if (isEmailError) "Usuario Desconocido" else null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AppTextFieldWithKeyboard(
                            value = password,
                            onValueChange = {
                                password = it
                                if (isPasswordError) validatePassword()
                            },
                            label = "Contraseña",
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = isPasswordError,
                            errorMessage = if (isPasswordError) "Contraseña Incorrecta" else null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { validateAndLogin() }
                            ),
                            modifier = Modifier.padding(bottom = 18.dp)
                        )

                        // Botón de inicio de sesión
                        AppButton(
                            text = "INICIAR SESIÓN",
                            onClick = { validateAndLogin() },
                            enabled = email.isNotEmpty() && password.isNotEmpty(),
                            loading = loginState is LoginState.Loading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Mostrar error de login
                        AnimatedVisibility(visible = loginState is LoginState.Error) {
                            AppCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = (loginState as? LoginState.Error)?.message ?: "",
                                    modifier = Modifier.padding(14.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Fila de botones para "Olvidaste la contraseña" y "Crear cuenta"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp), // Espaciado horizontal
                            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre los botones
                        ) {
                            TextButton(onClick = { /* Implementar recuperación */ }) {
                                Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.primary)
                            }

                            TextButton(onClick = { /* Implementar creación de cuenta */ }) {
                                Text("¿Crear cuenta?", color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // Botón de regreso a la pantalla principal
                        TextButton(
                            onClick = { onBackPressed() }, // Acción de regreso
                            modifier = Modifier.padding(vertical = 12.dp) // Espaciado vertical
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack, // Ícono de regreso
                                contentDescription = "Volver a la página principal",
                                modifier = Modifier.size(20.dp), // Tamaño del ícono
                                tint = MaterialTheme.colorScheme.primary // Color del ícono
                            )
                            Spacer(modifier = Modifier.width(8.dp)) // Espaciado entre el ícono y el texto
                            Text(
                                text = "Volver a la página principal",
                                color = MaterialTheme.colorScheme.primary, // Color del texto
                                style = MaterialTheme.typography.bodyMedium // Estilo del texto
                            )
                        }
                    }
                }

            }

            // Mostrar el indicador de carga encima del formulario
            if (loginState is LoginState.Loading) {
                ShowLoadingDialog(isLoading = true)
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess((loginState as LoginState.Success).user)
        }
    }
}

