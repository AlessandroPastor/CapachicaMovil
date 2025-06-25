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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import android.util.Patterns

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
import androidx.navigation.NavHostController
import com.example.turismomovile.R
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.presentation.components.AppButton
import com.example.turismomovile.presentation.components.AppCard
import com.example.turismomovile.presentation.components.AppTextFieldWithKeyboard
import com.example.turismomovile.presentation.components.FloatingBubblesBackground
import com.example.turismomovile.presentation.components.RotatingBackgroundLoginScreen
import com.example.turismomovile.presentation.components.ShowLoadingDialog
import com.example.turismomovile.presentation.theme.AppColors
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import io.dev.kmpventas.presentation.navigation.Routes
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
    sessionManager: SessionManager = koinInject()
) {

    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val validateEmail = {
        isEmailError = !email.contains("") || !email.contains("")
        isEmailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
    val scrollState = rememberScrollState()

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
                    if (isDarkMode)
                        MaterialTheme.colorScheme.background
                    else
                        Color.Transparent
                )
        ) {
            // Fondo animado con elementos de turismo
            FloatingBubblesBackground(
                modifier = Modifier.fillMaxSize()
            )

            // Contenido principal centrado perfectamente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(28.dp),
                            ambientColor = Color.Black.copy(alpha = 0.3f),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkMode)
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        else
                            Color.White.copy(alpha = 0.92f)
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (isDarkMode)
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        else
                            Color.Black.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState) // ⬅️ Habilita scroll vertical
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                    fontSize = 26.sp,
                                    letterSpacing = 1.2.sp
                                ),
                                color = if (isDarkMode)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Campos de entrada
                        AppTextFieldWithKeyboard(
                            value = email,
                            onValueChange = {
                                email = it
                                if (isEmailError) validateEmail()
                            },
                            label = "Usuario",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            },
                            isError = isEmailError,
                            errorMessage = if (isEmailError) "Usuario Desconocido" else null,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth()
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
                                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                        tint = if (isDarkMode)
                                            MaterialTheme.colorScheme.onSurface
                                        else
                                            MaterialTheme.colorScheme.primary
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
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Fila de botones: "¿Olvidaste tu contraseña?" y "¿Crear cuenta?"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isDarkMode)
                                        MaterialTheme.colorScheme.outline
                                    else
                                        MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            )  {
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 8.sp, // Puedes ajustarlo según necesidad
                                )
                            }


                            OutlinedButton(
                                onClick = { navController.navigate(Routes.REGISTER) },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isDarkMode)
                                        MaterialTheme.colorScheme.outline
                                    else
                                        MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "¿Crear cuenta?",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 8.sp,
                                )
                            }
                        }

                        // Botón de regreso a la pantalla principal
                        OutlinedButton(
                            onClick = { onBackPressed() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isDarkMode)
                                    MaterialTheme.colorScheme.outline
                                else
                                    MaterialTheme.colorScheme.primary
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDarkMode)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Volver a la página principal",
                                fontWeight = FontWeight.SemiBold
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