package com.example.turismomovile.presentation.screens.configuration.role.municipalidad

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescription
import com.example.turismomovile.data.remote.dto.configuracion.formatDateTime
import com.example.turismomovile.presentation.components.AppDialog
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.AppTheme
import com.example.turismomovile.presentation.theme.ThemeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@Composable
fun MunicipalidadScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: MunicipalidadViewModel = koinInject(),
    viewmodelDescription: MunicipalidadDescriptionViewModel = koinInject(),
    themeViewModel: ThemeViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stateDescription by viewmodelDescription.stateDescription.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMunicipalidad by remember { mutableStateOf<Municipalidad?>(null) }
    var selectedMunicipalidadDescription by remember { mutableStateOf<MunicipalidadDescription?>(null) }

    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle(
        initialValue = false,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    )
    // Manejo de notificaciones del viewModel de municipalidad
    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    // Manejo de notificaciones del viewModel de descripción
    LaunchedEffect(stateDescription.notification) {
        if (stateDescription.notification.isVisible) {
            notificationState.showNotification(
                message = stateDescription.notification.message,
                type = stateDescription.notification.type,
                duration = stateDescription.notification.duration
            )
        }
    }

    AppTheme(darkTheme = isDarkMode) {
        NotificationHost(state = notificationState) {
            Scaffold { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(70.dp))

                        // Barra de búsqueda mejorada
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                viewModel.loadMunicipalidad(searchQuery = it.ifEmpty { null })
                                viewmodelDescription.loadMunicipalidadDescription(searchQuery = it.ifEmpty { null })
                            },
                            placeholderText = "Buscar municipalidad...",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        when {
                            state.isLoading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }

                            state.items.isEmpty() -> {
                                EmptyStateMessage(
                                    message = "No se encontraron municipalidades",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.items, key = { it.id ?: "" }) { municipalidad ->
                                        MunicipalidadCard(
                                            m = municipalidad,
                                            onClick = { selectedMunicipalidad = municipalidad },
                                            onEditDescription = {
                                                val description = stateDescription.descriptions.find {
                                                    it.municipalidad_id == municipalidad.id
                                                }
                                                selectedMunicipalidadDescription = description ?: MunicipalidadDescription(
                                                    id = null,
                                                    municipalidad_id = municipalidad.id,
                                                    logo = "",
                                                    direccion = "",
                                                    descripcion = "",
                                                    ruc = "",
                                                    correo = "",
                                                    nombre_alcalde = "",
                                                    anio_gestion = ""
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Paginación mejorada
                        if (state.totalPages > 1) {
                            PaginationControls(
                                currentPage = state.currentPage,
                                totalPages = state.totalPages,
                                onPrevious = {
                                    viewModel.loadMunicipalidad(state.currentPage - 1, searchQuery)
                                    viewmodelDescription.loadMunicipalidadDescription(state.currentPage - 1, searchQuery)
                                },
                                onNext = {
                                    viewModel.loadMunicipalidad(state.currentPage + 1, searchQuery)
                                    viewmodelDescription.loadMunicipalidadDescription(state.currentPage + 1, searchQuery)
                                },
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogos
    selectedMunicipalidad?.let { municipalidad ->
        MunicipalidadDialog(
            municipalidad = municipalidad,
            onDismiss = { selectedMunicipalidad = null },
            onSave = { updated ->
                if (updated.id.isNullOrBlank()) {
                } else {
                    viewModel.updateMunicipalidad(updated)
                }
                selectedMunicipalidad = null
            }
        )
    }

    selectedMunicipalidadDescription?.let { description ->
        MunicipalidadDescriptionDialog(
            description = description,
            onDismiss = { selectedMunicipalidadDescription = null },
            onSave = { updated ->
                viewmodelDescription.updateMunicipalidadDescription(updated)
                selectedMunicipalidadDescription = null
            }
        )
    }
}

@Composable
private fun MunicipalidadCard(
    m: Municipalidad,
    onClick: () -> Unit,
    onEditDescription: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 2.dp,
        label = "cardElevation"
    )

    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = m.region ?: "Sin región",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                )

                ActionButtons(
                    onEdit = onClick,
                    onEditDescription = onEditDescription,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                MunicipalidadInfoRow(
                    icon = Icons.Filled.LocationOn,
                    text = "Distrito: ${m.distrito}",
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                MunicipalidadInfoRow(
                    icon = Icons.Filled.Map,
                    text = "Provincia: ${m.provincia}",
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                MunicipalidadInfoRow(
                    icon = Icons.Filled.Code,
                    text = "Código: ${m.codigo}",
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Fecha de creación",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Creado: ${m.createdAt?.formatDateTime() ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun MunicipalidadInfoRow(
    icon: ImageVector,
    text: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButtons(
    onEdit: () -> Unit,
    onEditDescription: () -> Unit,
    tint: Color
) {
    var editButtonPressed by remember { mutableStateOf(false) }

    val editButtonScale by animateFloatAsState(
        targetValue = if (editButtonPressed) 0.8f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "editButtonScale"
    )

    LaunchedEffect(editButtonPressed) {
        if (editButtonPressed) {
            delay(200)
            editButtonPressed = false
        }
    }

    Row {
        IconButton(
            onClick = {
                editButtonPressed = true
                onEdit()
            },
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer(
                    scaleX = editButtonScale,
                    scaleY = editButtonScale
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar Municipalidad",
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text("Editar descripción") } },
            state = rememberTooltipState()
        ) {
            IconButton(
                onClick = onEditDescription,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = "Editar Descripción",
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Composable
private fun MunicipalidadDialog(
    municipalidad: Municipalidad,
    onDismiss: () -> Unit,
    onSave: (Municipalidad) -> Unit
) {
    var distrito by remember { mutableStateOf(municipalidad.distrito ?: "") }
    var provincia by remember { mutableStateOf(municipalidad.provincia ?: "") }
    var region by remember { mutableStateOf(municipalidad.region ?: "") }
    var codigo by remember { mutableStateOf(municipalidad.codigo ?: "") }

    val camposValidos = listOf(distrito, provincia, region, codigo).all { it.isNotBlank() }

    AppDialog(
        title = if (municipalidad.id.isNullOrBlank()) "Nueva Municipalidad" else "Editar Municipalidad",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        municipalidad.copy(
                            distrito = distrito,
                            provincia = provincia,
                            region = region,
                            codigo = codigo
                        )
                    )
                },
                enabled = camposValidos
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = distrito,
                onValueChange = { distrito = it },
                label = { Text("Distrito *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = provincia,
                onValueChange = { provincia = it },
                label = { Text("Provincia *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                label = { Text("Región *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
private fun MunicipalidadDescriptionDialog(
    description: MunicipalidadDescription,
    onDismiss: () -> Unit,
    onSave: (MunicipalidadDescription) -> Unit
) {
    var logo by remember { mutableStateOf(description.logo) }
    var direccion by remember { mutableStateOf(description.direccion) }
    var descripcion by remember { mutableStateOf(description.descripcion) }
    var ruc by remember { mutableStateOf(description.ruc) }
    var correo by remember { mutableStateOf(description.correo) }
    var nombreAlcalde by remember { mutableStateOf(description.nombre_alcalde) }
    var anioGestion by remember { mutableStateOf(description.anio_gestion) }

    val camposValidos = listOf(
        logo, direccion, descripcion,
        ruc, correo, nombreAlcalde, anioGestion
    ).all { !it.isNullOrBlank() }

    AppDialog(
        title = if (description.id.isNullOrBlank()) "Nueva Descripción" else "Editar Descripción",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        description.copy(
                            logo = logo,
                            direccion = direccion,
                            descripcion = descripcion,
                            ruc = ruc,
                            correo = correo,
                            nombre_alcalde = nombreAlcalde,
                            anio_gestion = anioGestion
                        )
                    )
                },
                enabled = camposValidos
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = logo ?: "",
                onValueChange = { logo = it },
                label = { Text("Logo *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion ?: "",
                onValueChange = { direccion = it },
                label = { Text("Dirección *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion ?: "",
                onValueChange = { descripcion = it },
                label = { Text("Descripción *") },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = ruc ?: "",
                onValueChange = { ruc = it },
                label = { Text("RUC *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = correo ?: "",
                onValueChange = { correo = it },
                label = { Text("Correo *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nombreAlcalde ?: "",
                onValueChange = { nombreAlcalde = it },
                label = { Text("Nombre del Alcalde *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = anioGestion ?: "",
                onValueChange = { anioGestion = it },
                label = { Text("Año de Gestión *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholderText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onPrevious,
            enabled = currentPage > 0,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.NavigateBefore, null)
        }

        Text(
            text = "Página ${currentPage + 1} de $totalPages",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        FilledTonalIconButton(
            onClick = onNext,
            enabled = (currentPage + 1) < totalPages,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.NavigateNext, null)
        }
    }
}

@Composable
private fun EmptyStateMessage(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


