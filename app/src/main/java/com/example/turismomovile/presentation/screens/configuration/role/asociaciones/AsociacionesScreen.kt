package com.example.turismomovile.presentation.screens.configuration.role.asociaciones

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociaciones
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.toCreateDTO
import com.example.turismomovile.presentation.components.AppDialog
import com.example.turismomovile.presentation.components.AppEmptyState
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.SearchBarSimple
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import org.koin.compose.koinInject


@Composable
fun AsociacionesScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: AsociacionesViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stateMuni by viewModel.stateMuni.collectAsStateWithLifecycle()
    val stateImgAso by viewModel.stateImgAso.collectAsStateWithLifecycle()

    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedAsociacion by remember { mutableStateOf<Asociacion?>(null) }
    var selectedImgAsociacion by remember { mutableStateOf<ImgAsociaciones?>(null) }
    var showImgDialog by remember { mutableStateOf(false) }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        viewModel.loadMunicipalidad()
        viewModel.loadAllAsociaciones()
    }

    LaunchedEffect(state.notification) {
        state.notification.takeIf { it.isVisible }?.let {
            notificationState.showNotification(
                message = it.message,
                type = it.type,
                duration = it.duration
            )
        }
    }

    NotificationHost(state = notificationState) {

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { selectedAsociacion = Asociacion() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        ) {
            innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(76.dp))
                    SearchBarSimple(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.loadAllAsociaciones(searchQuery = it.ifEmpty { null })
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        state.itemsAso.isEmpty() -> AppEmptyState(title = "No se encontraron Resultados" )
                        else -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.itemsAso, key = { it.id ?: "" }) { asociacion ->
                                    AsociacionCard(
                                        asociacion = asociacion,
                                        municipalidades = stateMuni.items,
                                        imagenes = stateImgAso.items.filter { it.asociacion_id == asociacion.id },
                                        onClick = { selectedAsociacion = asociacion },
                                        onDelete = { viewModel.deleteAsociaciones(asociacion.id!!) },
                                        onAddImage = {
                                            selectedImgAsociacion = ImgAsociaciones(
                                                asociacion.id!!,
                                                asociacion_id = TODO(),
                                                url_image = TODO(),
                                                estado = TODO(),
                                                codigo = TODO(),
                                                created_at = TODO(),
                                                updated_at = TODO()
                                            )
                                            showImgDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    PaginationControls(
                        currentPage = state.currentPage,
                        totalPages = state.totalPages,
                        onPrevious = {
                            if (state.currentPage > 0) {
                                viewModel.loadAllAsociaciones((state.currentPage - 1).toString())
                            }
                        },
                        onNext = {
                            if (state.currentPage < state.totalPages - 1) {
                                viewModel.loadAllAsociaciones((state.currentPage + 1).toString())
                            }
                        }
                    )

                }
            }
        }
    }

    // Diálogo para Asociación
    selectedAsociacion?.let { asociacion ->
        AsociacionDialog(
            asociacion = asociacion,
            municipalidades = stateMuni.items,
            onDismiss = { selectedAsociacion = null },
            onSave = { updatedAsociacion ->
                if (updatedAsociacion.id.isNullOrBlank()) {
                    updatedAsociacion.toCreateDTO()?.let { dto ->
                        viewModel.createAsociaciones(dto)
                    }
                } else {
                    viewModel.updateAsociaciones(updatedAsociacion)
                }
                selectedAsociacion = null
            }
        )
    }

    // Diálogo para Imagen de Asociación
    if (showImgDialog) {
        ImgAsociacionDialog(
            imgAsociacion = selectedImgAsociacion,
            onDismiss = { showImgDialog = false },
            onSave = { imgAsociacion ->
                if (imgAsociacion.id.isNullOrBlank()) {
                    imgAsociacion.toCreateDTO()?.let { dto ->
                        viewModel.createImgAsociaciones(dto)
                    }
                } else {
                    viewModel.updateImgAsociaciones(imgAsociacion)
                }
                showImgDialog = false
            }
        )
    }
}

// Componentes mejorados

@Composable
fun AsociacionCard(
    asociacion: Asociacion,
    municipalidades: List<Municipalidad>,
    imagenes: List<ImgAsociaciones>,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onAddImage: () -> Unit
) {
    val municipalidad = municipalidades.firstOrNull { it.id == asociacion.municipalidadId }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asociacion.nombre ?: "Sin nombre",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    municipalidad?.let {
                        Text(
                            text = "Municipalidad: ${it.distrito}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = "Lugar: ${asociacion.lugar ?: "No especificado"}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (imagenes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ImageSlider(imagenes = imagenes)
                    }
                }

                Column {
                    IconButton(onClick = onClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onAddImage) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Agregar imagen")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Descripción: ${asociacion.descripcion ?: "No disponible"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSlider(imagenes: List<ImgAsociaciones>) {
    val pagerState = rememberPagerState(pageCount = { imagenes.size })

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
        ) { page ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = imagenes[page].url_image,
                    contentDescription = "Imagen ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(imagenes.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

@Composable
fun AsociacionDialog(
    asociacion: Asociacion,
    municipalidades: List<Municipalidad>,
    onDismiss: () -> Unit,
    onSave: (Asociacion) -> Unit
) {
    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf(asociacion.nombre ?: "") }
    var lugar by remember { mutableStateOf(asociacion.lugar ?: "") }
    var descripcion by remember { mutableStateOf(asociacion.descripcion ?: "") }
    var selectedMunicipalidadId by remember { mutableStateOf(asociacion.municipalidadId ?: "") }
    var estado by remember { mutableStateOf(asociacion.estado ?: true) }

    // Estados para los errores de validación
    var errors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    // Función para validar todos los campos
    fun validateFields(): Boolean {
        val newErrors = mutableMapOf<String, String?>()

        if (nombre.isBlank()) newErrors["nombre"] = "El nombre es requerido"
        if (lugar.isBlank()) newErrors["lugar"] = "El lugar es requerido"
        if (descripcion.isBlank()) newErrors["descripcion"] = "La descripción es requerida"
        if (selectedMunicipalidadId.isBlank()) newErrors["municipalidad_id"] = "Seleccione una municipalidad"

        errors = newErrors
        return newErrors.isEmpty()
    }

    AppDialog(
        title = if (asociacion.id.isNullOrBlank()) "Nueva Asociación" else "Editar Asociación",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (validateFields()) {
                        onSave(
                            asociacion.copy(
                                nombre = nombre,
                                lugar = lugar,
                                descripcion = descripcion,
                                municipalidadId = selectedMunicipalidadId,
                                estado = estado
                            )
                        )
                    }
                }
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
            // Selector de Municipalidad con validación
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = municipalidades.firstOrNull { it.id == selectedMunicipalidadId }?.distrito
                        ?: "Seleccionar municipalidad",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Municipalidad *") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    isError = errors.containsKey("municipalidad_id"),
                    supportingText = {
                        errors["municipalidad_id"]?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    municipalidades.forEach { muni ->
                        DropdownMenuItem(
                            text = { Text(muni.distrito ?: "Sin nombre") },
                            onClick = {
                                selectedMunicipalidadId = muni.id ?: ""
                                expanded = false
                                // Limpiar error al seleccionar
                                errors = errors - "municipalidad_id"
                            }
                        )
                    }
                }
            }

            // Campo Nombre con validación
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    // Limpiar error al escribir
                    if (it.isNotBlank()) errors = errors - "nombre"
                },
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("nombre"),
                supportingText = {
                    errors["nombre"]?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo Lugar con validación
            OutlinedTextField(
                value = lugar,
                onValueChange = {
                    lugar = it
                    if (it.isNotBlank()) errors = errors - "lugar"
                },
                label = { Text("Lugar *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errors.containsKey("lugar"),
                supportingText = {
                    errors["lugar"]?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Campo Descripción con validación
            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    if (it.isNotBlank()) errors = errors - "descripcion"
                },
                label = { Text("Descripción *") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                isError = errors.containsKey("descripcion"),
                supportingText = {
                    errors["descripcion"]?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Switch de Estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado:", modifier = Modifier.padding(end = 8.dp))
                Switch(
                    checked = estado,
                    onCheckedChange = { estado = it }
                )
                Text(if (estado) "Activo" else "Inactivo")
            }
        }
    }
}

@Composable
fun ImgAsociacionDialog(
    imgAsociacion: ImgAsociaciones?,
    onDismiss: () -> Unit,
    onSave: (ImgAsociaciones) -> Unit
) {
    var codigo by remember { mutableStateOf(imgAsociacion?.codigo ?: "") }
    var urlImage by remember { mutableStateOf(imgAsociacion?.url_image ?: "") }
    var estado by remember { mutableStateOf(imgAsociacion?.estado ?: "true") }

    val camposValidos = codigo.isNotBlank() && urlImage.isNotBlank()

    AppDialog(
        title = if (imgAsociacion?.id.isNullOrBlank()) "Nueva Imagen" else "Editar Imagen",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        imgAsociacion?.copy(
                            codigo = codigo,
                            url_image = urlImage,
                            estado = estado
                        ) ?: ImgAsociaciones(
                            id = "",
                            codigo = codigo,
                            url_image = urlImage,
                            estado = estado,
                            asociacion_id = imgAsociacion?.asociacion_id ?: "",
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
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = urlImage,
                onValueChange = { urlImage = it },
                label = { Text("URL de la imagen") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado:", modifier = Modifier.padding(end = 8.dp))
                Switch(
                    checked = estado.toBoolean(),
                    onCheckedChange = { estado = it.toString() }
                )
                Text(if (estado.toBoolean()) "Activo" else "Inactivo")
            }

            if (urlImage.isNotBlank()) {
                AsyncImage(
                    model = urlImage,
                    contentDescription = "Vista previa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Anterior
        IconButton(
            onClick = onPrevious,
            enabled = currentPage > 0,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                contentDescription = "Página anterior",
                tint = if (currentPage > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }

        // Indicador de página
        Text(
            text = "Página ${currentPage + 1} de $totalPages",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Botón Siguiente
        IconButton(
            onClick = onNext,
            enabled = currentPage < totalPages - 1,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = "Página siguiente",
                tint = if (currentPage < totalPages - 1) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }
    }
}