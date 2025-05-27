package com.example.turismomovile.presentation.screens.configuration.role.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.data.remote.dto.configuracion.ModuleCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ModuleDTO
import com.example.turismomovile.data.remote.dto.configuracion.ParentModule
import com.example.turismomovile.data.remote.dto.configuracion.toModuleDTO
import com.example.turismomovile.data.remote.dto.formatDateTime
import com.example.turismomovile.presentation.components.AppButton
import com.example.turismomovile.presentation.components.AppDialog
import com.example.turismomovile.presentation.components.AppTextField
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.StatisticCard
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.theme.LocalAppDimens
import org.koin.compose.koinInject

@Composable
fun ModuleScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: ModuleViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    NotificationHost(state = notificationState) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(LocalAppDimens.current.spacing_16.dp)
            ) {
                // Estadísticas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatisticCard(
                        title = "Total Módulos",
                        value = state.totalElements.toString(),
                        icon = Icons.Default.Folder,
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                    )
                    StatisticCard(
                        title = "Módulos Activos",
                        value = state.items.count { it.status }.toString(),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                    )
                    StatisticCard(
                        title = "Módulos Inactivos",
                        value = state.items.count { !it.status }.toString(),
                        icon = Icons.Default.Cancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                    )
                }

                // Barra de herramientas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.loadModules(searchQuery = it.ifEmpty { null })
                        },
                        placeholder = { Text("Buscar módulos...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    AppButton(
                        text = "Nuevo Módulo",
                        onClick = {
                            viewModel.setSelectedModule(ModuleDTO(title = "", subtitle = "", type = "", status = true))
                        },
                        icon = Icons.Default.Add,
                        modifier = Modifier.widthIn(min = 180.dp)
                    )
                }

                // Tabla de módulos
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        // Encabezados
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "NOMBRE",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(3f)
                            )
                            Text(
                                text = "CÓDIGO",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(2.8f)
                            )
                            Text(
                                text = "ESTADO",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(3f)
                            )
                            Text(
                                text = "FECHA CREACIÓN",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(3f)
                            )
                            Text(
                                text = "ACCIONES",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(2.5f),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty())
                                        "No hay módulos disponibles"
                                    else
                                        "No se encontraron resultados para '$searchQuery'",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(state.items) { module ->
                                    ModuleRow(
                                        module = module,
                                        onEdit = { viewModel.setSelectedModule(module) },
                                        onDelete = { viewModel.deleteModule(module.id ?: "") }
                                    )
                                }
                            }
                        }
                    }
                }

                // Paginación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.previousPage() },
                        enabled = state.currentPage > 0, // Solo habilita si hay páginas previas
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Icon(
                            Icons.Default.NavigateBefore,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Anterior")
                    }

                    Text(
                        text = "${state.currentPage + 1} de ${state.totalPages}",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = { viewModel.nextPage() },
                        enabled = state.currentPage + 1 < state.totalPages, // Solo habilita si hay más páginas
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Text("Siguiente")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.NavigateNext,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (state.isDialogOpen) {
            ModuleDialog(
                module = state.selectedItem,
                parentModules = state.parentModules,
                onDismiss = { viewModel.closeDialog() },
                onSave = { moduleCreateDTO ->
                    val moduleDTO = moduleCreateDTO.toModuleDTO() // Convierte a ModuleDTO

                    if (!state.selectedItem?.id.isNullOrEmpty()) {
                        // Si hay un ID, se actualiza el módulo
                        state.selectedItem?.id?.let { moduleId ->
                            viewModel.updateModule(moduleDTO.copy(id = moduleId))
                        }
                    } else {
                        // Si no hay ID, se crea un nuevo módulo
                        viewModel.createModule(moduleDTO)
                    }
                }
            )
        }

    }
}


@Composable
private fun ModuleRow(
    module: ModuleDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val createdAt = module.createdAt?.let { formatDateTime(it) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = module.link ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = module.parentModule?.title ?: "Sin módulo padre",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = module.code ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(1.5f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = if (module.status)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (module.status) "Activo" else "Inactivo",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (module.status)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        if (createdAt != null) {
            Text(
                text = createdAt,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.weight(1.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    Divider()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDialog(
    module: ModuleDTO?,
    parentModules: List<ParentModule>,
    onDismiss: () -> Unit,
    onSave: (ModuleCreateDTO) -> Unit
) {
    var title by remember { mutableStateOf(module?.title ?: "") }
    var subtitle by remember { mutableStateOf(module?.subtitle ?: "") }
    var type by remember { mutableStateOf(module?.type ?: "") }
    var icon by remember { mutableStateOf(module?.icon ?: "") }
    var link by remember { mutableStateOf(module?.link ?: "") }
    var moduleOrder by remember { mutableStateOf(module?.moduleOrder?.toString() ?: "0") }
    var status by remember { mutableStateOf(module?.status ?: true) }
    var selected by remember { mutableStateOf((module?.let { it as? ModuleCreateDTO })?.selected ?: true) }


    // ✅ `parentModuleId` correctamente manejado
    var selectedParent by remember { mutableStateOf(module?.parentModule?.id ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AppDialog(
        title = if (module?.id.isNullOrEmpty()) "Nuevo Módulo" else "Editar Módulo",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val moduleToSave = ModuleCreateDTO(
                        title = title,
                        subtitle = subtitle,
                        type = type,
                        icon = icon,
                        status = status,
                        selected = selected,
                        link = link,
                        moduleOrder = moduleOrder.toIntOrNull() ?: 0,
                        parentModuleId = selectedParent.ifEmpty { "N/A" }
                    )
                    onSave(moduleToSave)
                },
                enabled = title.isNotEmpty()
            ) {
                Text("Guardar", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔹 Campo de título
            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Título",
                leadingIcon = { Icon(imageVector = Icons.Default.Title, contentDescription = null) }
            )

            // 🔹 Campo de subtítulo
            AppTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = "Subtítulo",
                leadingIcon = { Icon(imageVector = Icons.Default.ShortText, contentDescription = null) }
            )

            // 🔹 Campo de tipo
            AppTextField(
                value = type,
                onValueChange = { type = it },
                label = "Tipo",
                leadingIcon = { Icon(imageVector = Icons.Default.Category, contentDescription = null) }
            )

            // 🔹 Campo de icono
            AppTextField(
                value = icon,
                onValueChange = { icon = it },
                label = "Ícono",
                leadingIcon = { Icon(imageVector = Icons.Default.Image, contentDescription = null) }
            )

            // 🔹 Campo de link
            AppTextField(
                value = link,
                onValueChange = { link = it },
                label = "Enlace",
                leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null) }
            )

            // 🔹 Campo de orden del módulo
            AppTextField(
                value = moduleOrder,
                onValueChange = { moduleOrder = it },
                label = "Orden del Módulo",
                leadingIcon = { Icon(imageVector = Icons.Default.List, contentDescription = null) },
            )

            // 🔽 Selector de módulo padre con UI mejorada
            Column {
                Text(
                    text = "Módulo Padre",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = parentModules.find { it.id == selectedParent }?.title
                            ?: "Selecciona un Módulo Padre",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Módulo Padre") },
                        leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .focusRequester(focusRequester),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin módulo padre", style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            },
                            onClick = {
                                selectedParent = ""
                                expanded = false
                            }
                        )
                        Divider()
                        parentModules.forEach { parent ->
                            DropdownMenuItem(
                                text = { Text(parent.title, style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                onClick = {
                                    selectedParent = parent.id ?: ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 🔘 Estado del módulo (Activo/Inactivo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Estado:", style = MaterialTheme.typography.bodyMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = status,
                        onCheckedChange = { status = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (status) "Activo" else "Inactivo", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ✅ CheckBox para `selected`
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { selected = it }
                )
                Text("Seleccionado", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


