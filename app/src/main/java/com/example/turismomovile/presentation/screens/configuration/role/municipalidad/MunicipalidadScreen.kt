package io.dev.kmpventas.presentation.screens.configuration.role.municipalidad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.formatDateTime
import com.example.turismomovile.presentation.components.AppDialog

import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import com.example.turismomovile.presentation.screens.configuration.role.municipalidad.MunicipalidadViewModel
import org.koin.compose.koinInject


@Composable
fun MunicipalidadScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: MunicipalidadViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMunicipalidad by remember { mutableStateOf<Municipalidad?>(null) }
    var itemToDelete by remember { mutableStateOf<Municipalidad?>(null) }
    var itemToEdit by remember { mutableStateOf<Municipalidad?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }


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
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedMunicipalidad = Municipalidad(
                            id = "",
                            distrito = "",
                            provincia = "",
                            region = "",
                            codigo = "",
                            createdAt = "",
                            updatedAt = "",
                            deletedAt = null
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Municipalidad")
                }



            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(60.dp))

                    SearchBarSimple(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.loadMunicipalidad(searchQuery = it.ifEmpty { null })
                        },
                        placeholderText = "Buscar municipalidad"
                    )


                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        state.isLoading -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        state.items.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron municipalidades.")
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.items, key = { it.id ?: "" }) { municipalidad ->
                                    MunicipalidadCard(
                                        m = municipalidad,
                                        onClick = { selectedMunicipalidad = municipalidad },
                                        onDelete = { municipalidad.id?.let {
                                            viewModel.deleteMunicipalidad(
                                                it
                                            )
                                            showDeleteDialog = true
                                        } }
                                    )
                                }
                            }
                        }
                    }

                    // Paginación
                    if (state.totalPages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { viewModel.loadMunicipalidad(state.currentPage - 1, searchQuery) },
                                enabled = state.currentPage > 0
                            ) {
                                Icon(Icons.AutoMirrored.Filled.NavigateBefore, null)
                                Text("Anterior")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Página ${state.currentPage + 1} de ${state.totalPages}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = { viewModel.loadMunicipalidad(state.currentPage + 1, searchQuery) },
                                enabled = (state.currentPage + 1) < state.totalPages
                            ) {
                                Text("Siguiente")
                                Icon(Icons.AutoMirrored.Filled.NavigateNext, null)
                            }
                        }
                    }
                }
            }
        }
    }
    if (selectedMunicipalidad != null) {
        MunicipalidadDialog(
            municipalidad = selectedMunicipalidad,
            onDismiss = { selectedMunicipalidad = null },
            onSave = { municipalidad ->
                if (municipalidad.id.isNullOrBlank()) {
                    // ✅ Convertimos a DTO aquí antes de enviarlo al ViewModel
                    municipalidad.distrito?.let {
                        municipalidad.provincia?.let { it1 ->
                            municipalidad.region?.let { it2 ->
                                municipalidad.codigo?.let { it3 ->
                                    MunicipalidadCreateDTO(
                                        distrito = it,
                                        provincia = it1,
                                        region = it2,
                                        codigo = it3
                                    )
                                }
                            }
                        }
                    }?.let {
                        viewModel.createMunicipalidad(
                            it
                        )
                    }

                }  else {
                    viewModel.updateMunicipalidad(municipalidad)
                }
                selectedMunicipalidad = null
            }
        )
    }



}
@Composable
fun MunicipalidadDialog(
    municipalidad: Municipalidad?,
    onDismiss: () -> Unit,
    onSave: (Municipalidad) -> Unit
) {
    var distrito by remember { mutableStateOf(municipalidad?.distrito ?: "") }
    var provincia by remember { mutableStateOf(municipalidad?.provincia ?: "") }
    var region by remember { mutableStateOf(municipalidad?.region ?: "") }
    var codigo by remember { mutableStateOf(municipalidad?.codigo ?: "") }
    val camposValidos = distrito.isNotBlank() && provincia.isNotBlank() && region.isNotBlank() && codigo.isNotBlank()

    AppDialog(
        title = if (municipalidad?.id.isNullOrBlank()) "Nueva Municipalidad" else "Editar Municipalidad",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Municipalidad(
                            id = municipalidad?.id ?: "",
                            distrito = distrito,
                            provincia = provincia,
                            region = region,
                            codigo = codigo,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = distrito,
                onValueChange = { distrito = it },
                label = { Text("Distrito") },
                singleLine = true
            )
            OutlinedTextField(
                value = provincia,
                onValueChange = { provincia = it },
                label = { Text("Provincia") },
                singleLine = true
            )
            OutlinedTextField(
                value = region,
                onValueChange = { region = it },
                label = { Text("Región") },
                singleLine = true
            )
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código") },
                singleLine = true
            )
        }
    }
}


@Composable
fun MunicipalidadCard(
    m: Municipalidad,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline
    val surface = MaterialTheme.colorScheme.surface

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                m.region?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = primary
                    )
                }
                Text("Distrito: ${m.distrito}", style = MaterialTheme.typography.bodyMedium)
                Text("Provincia: ${m.provincia}", style = MaterialTheme.typography.bodySmall)
                Text("Código: ${m.codigo}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Creado: ${m.createdAt?.formatDateTime() ?: "N/A"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = outline
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onClick) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}


@Composable
fun SearchBarSimple(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Buscar..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        placeholder = { Text(placeholderText) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}


