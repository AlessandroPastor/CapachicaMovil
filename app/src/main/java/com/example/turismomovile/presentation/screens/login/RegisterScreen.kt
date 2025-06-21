package com.example.turismomovile.presentation.screens.login

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.R
import com.example.turismomovile.data.remote.dto.LoginInput
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.presentation.components.AppButton
import com.example.turismomovile.presentation.components.AppTextFieldWithKeyboard
import com.example.turismomovile.presentation.components.ShowLoadingDialog
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import com.example.turismomovile.presentation.components.RotatingBackgroundLoginScreen
import com.example.turismomovile.presentation.components.AppCard
import com.example.turismomovile.presentation.components.FloatingBubblesBackground
import com.example.turismomovile.presentation.theme.AppColors
import io.dev.kmpventas.presentation.navigation.Routes
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val validateEmail = {
        isEmailError = !email.contains("@") || email.isEmpty()
        !isEmailError
    }

    val validatePassword = {
        isPasswordError = password.length < 3
        !isPasswordError
    }

    val validateAndRegister = {
        if (validateEmail() && validatePassword()) {
            keyboardController?.hide()
            viewModel.register(LoginInput(name, lastName, username, email, password))
        }
    }

    LaunchedEffect(registerState) {
        if (registerState is RegisterViewModel.RegisterState.Success) {
            val userResponse = (registerState as RegisterViewModel.RegisterState.Success).response.data.user
            val user = User(
                id = userResponse.id.toString(),
                email = userResponse.email,
                name = userResponse.username,
                token = (registerState as RegisterViewModel.RegisterState.Success).response.data.token
            )

            // Llamamos al éxito del registro
            onRegisterSuccess(user)

            // Redirigir al HOME después de crear la cuenta
            navController.navigate(Routes.HOME) {
                popUpTo("login") { inclusive = true }
            }
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Título con icono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = if (isDarkMode)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Registro",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isDarkMode)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "Turismo Capachica",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = if (isDarkMode)
                                MaterialTheme.colorScheme.onSurface
                            else
                                Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Campos de entrada
                        AppTextFieldWithKeyboard(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nombre",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = "Apellido",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = username,
                            onValueChange = { username = it },
                            label = "Usuario",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = email,
                            onValueChange = {
                                email = it
                                if (isEmailError) validateEmail()
                            },
                            label = "Correo electrónico",
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
                            errorMessage = if (isEmailError) "Correo no válido" else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = password,
                            onValueChange = {
                                password = it
                                if (isPasswordError) validatePassword()
                            },
                            label = "Contraseña",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isDarkMode)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Mostrar/Ocultar Contraseña",
                                        tint = if (isDarkMode)
                                            MaterialTheme.colorScheme.onSurface
                                        else
                                            MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = isPasswordError,
                            errorMessage = if (isPasswordError) "Mínimo 3 caracteres" else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón de registro
                        Button(
                            onClick = { validateAndRegister() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = email.isNotEmpty() && password.isNotEmpty(),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (registerState is RegisterViewModel.RegisterState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "REGISTRARSE",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                        }

                        // Mostrar error de registro
                        if (registerState is RegisterViewModel.RegisterState.Error) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ) {
                                Text(
                                    text = (registerState as RegisterViewModel.RegisterState.Error).message,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "¿Ya tienes una cuenta? Inicia sesión",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (registerState is RegisterViewModel.RegisterState.Loading) {
                ShowLoadingDialog(isLoading = true)
            }
        }
    }

    LaunchedEffect(registerState) {
        if (registerState is RegisterViewModel.RegisterState.Success) {
            val userResponse = (registerState as RegisterViewModel.RegisterState.Success).response.data.user
            val user = User(
                id = userResponse.id.toString(),
                email = userResponse.email,
                name = userResponse.username,
                token = (registerState as RegisterViewModel.RegisterState.Success).response.data.token
            )
            onRegisterSuccess(user)
        }
    }
}
