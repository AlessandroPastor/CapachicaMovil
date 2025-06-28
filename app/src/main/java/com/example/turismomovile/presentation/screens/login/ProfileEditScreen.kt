package com.example.turismomovile.presentation.screens.login

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turismomovile.R
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.dto.ProfileForm
import com.example.turismomovile.data.remote.dto.UpdateProfileDTO
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.NotificationType
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.screens.dashboard.HomeViewModel
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
    sessionManager: SessionManager,
    onProfileUpdated: () -> Unit = {},
    themeViewModel: ThemeViewModel = koinInject()
) {
    val notificationState = rememberNotificationState()
    val state = viewModel.editState.collectAsState().value
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(false)
    val context = LocalContext.current

    // Estados para la imagen y formulario
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadResult by remember { mutableStateOf<String?>(null) }
    var initialLoaded by remember { mutableStateOf(false) }
    var form by remember { mutableStateOf(ProfileForm()) }

    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Cargar datos iniciales del usuario
    LaunchedEffect(Unit) {
        if (!initialLoaded) {
            val user = withContext(Dispatchers.IO) { sessionManager.getUser() }
            user?.let {
                form = ProfileForm(
                    name = it.name ?: "",
                    lastName = it.last_name ?: "",
                    code = it.code ?: "",
                    username = it.username ?: "",
                    email = it.email ?: "",
                    imagenUrl = it.imagenUrl ?: ""
                )
            }
            initialLoaded = true
        }
    }

    // Subida automática al seleccionar imagen
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            val imageBytes = viewModel.getBytesFromUri(context, uri)
            if (imageBytes != null) {
                viewModel.uploadProfileImage(imageBytes, "profile_photo.jpg") { url ->
                    if (url != null) {
                        form = form.copy(imagenUrl = url)
                        uploadResult = "Imagen subida correctamente"
                    } else {
                        uploadResult = "Error al subir la imagen"
                    }
                }
            } else {
                uploadResult = "No se pudo leer la imagen"
            }
        }
    }

    // Notificaciones del ViewModel
    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    // Notificación de éxito post-actualización
    LaunchedEffect(state.success) {
        if (state.success) {
            notificationState.showNotification(
                message = "¡Perfil actualizado correctamente!",
                type = NotificationType.SUCCESS,
                duration = 2500
            )
            onProfileUpdated()
        }
    }

    AppTheme(darkTheme = isDarkMode) {
        NotificationHost(state = notificationState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Header con título
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Editar Perfil",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Actualiza tu información personal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Sección de imagen de perfil mejorada
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Contenedor de imagen con efecto
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .shadow(12.dp, CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Imagen seleccionada",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (form.imagenUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = form.imagenUrl,
                                        contentDescription = "Imagen actual",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(R.drawable.logo_capachica),
                                        contentDescription = "Imagen por defecto",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            // Botón flotante mejorado
                            FloatingActionButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset((-8).dp, (-8).dp)
                                    .shadow(8.dp, CircleShape),
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Cambiar imagen",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Mensaje de subida mejorado
                        uploadResult?.let { result ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (result.startsWith("Error"))
                                        MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = result,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (result.startsWith("Error"))
                                        MaterialTheme.colorScheme.onErrorContainer
                                    else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Campos del formulario mejorados
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Información Personal",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            StyledTextField(
                                value = form.name,
                                onValueChange = { form = form.copy(name = it) },
                                label = "Nombre",
                                icon = Icons.Default.Person
                            )

                            StyledTextField(
                                value = form.lastName,
                                onValueChange = { form = form.copy(lastName = it) },
                                label = "Apellido",
                                icon = Icons.Default.Person
                            )

                            StyledTextField(
                                value = form.code,
                                onValueChange = { form = form.copy(code = it) },
                                label = "Código",
                                icon = Icons.Default.Code
                            )

                            StyledTextField(
                                value = form.username,
                                onValueChange = { form = form.copy(username = it) },
                                label = "Nombre de usuario",
                                icon = Icons.Default.AccountCircle
                            )

                            StyledTextField(
                                value = form.email,
                                onValueChange = { form = form.copy(email = it) },
                                label = "Correo electrónico",
                                icon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email
                            )

                            StyledTextField(
                                value = form.imagenUrl,
                                onValueChange = { form = form.copy(imagenUrl = it) },
                                label = "URL de la imagen",
                                icon = Icons.Default.Image,
                                readOnly = true
                            )
                        }
                    }

                    // Botón de guardar mejorado
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateProfile(
                                    UpdateProfileDTO(
                                        name = form.name,
                                        last_name = form.lastName,
                                        code = form.code,
                                        username = form.username,
                                        email = form.email,
                                        imagen_url = form.imagenUrl
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            enabled = !state.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (state.isLoading) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        "Guardando...",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            } else {
                                Text(
                                    "Guardar Cambios",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}