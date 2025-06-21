package com.example.turismomovile.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import io.dev.kmpventas.presentation.navigation.Routes
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: RegisterViewModel = koinInject()
) {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

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

    AppTheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Fondo animado de imágenes
                val images = listOf(
                    R.drawable.escallani,
                    R.drawable.festilavallago,
                    R.drawable.chifron,
                )
                RotatingBackgroundLoginScreen(images = images)

                // Tarjeta contenedora del formulario
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .widthIn(max = 500.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Título con icono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Registro",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Text(
                            text = "Turismo Capachica",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
// Campos de entrada
                        AppTextFieldWithKeyboard(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nombre",
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = "Apellido",
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = username,
                            onValueChange = { username = it },
                            label = "Usuario",
                            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AppTextFieldWithKeyboard(
                            value = email,
                            onValueChange = {
                                email = it
                                if (isEmailError) validateEmail()
                            },
                            label = "Correo electrónico",
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
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
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Mostrar/Ocultar Contraseña",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                        fontWeight = FontWeight.Bold
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

                        // Enlaces inferiores
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = { navController.navigate(Routes.LOGIN) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "¿Ya tienes cuenta? Inicia sesión",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = { onBackPressed() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Volver al inicio")
                            }
                        }
                    }
                }

                // Mostrar el indicador de carga
                if (registerState is RegisterViewModel.RegisterState.Loading) {
                    ShowLoadingDialog(isLoading = true)
                }
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
