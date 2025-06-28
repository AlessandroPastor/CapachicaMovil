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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Sección de imagen de perfil
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
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
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }

                    // Botón flotante para cambiar imagen
                    FloatingActionButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset((-16).dp, (-16).dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cambiar imagen"
                        )
                    }
                }
                // Mensaje de subida
                uploadResult?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (it.startsWith("Error")) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Campos del formulario
                OutlinedTextField(
                    value = form.name,
                    onValueChange = { form = form.copy(name = it) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = form.lastName,
                    onValueChange = { form = form.copy(lastName = it) },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = form.code,
                    onValueChange = { form = form.copy(code = it) },
                    label = { Text("Código") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = form.username,
                    onValueChange = { form = form.copy(username = it) },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = form.email,
                    onValueChange = { form = form.copy(email = it) },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = form.imagenUrl,
                    onValueChange = { form = form.copy(imagenUrl = it) },
                    label = { Text("URL de la imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de guardar
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
                        .height(56.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar cambios")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}