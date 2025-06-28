package com.example.turismomovile.presentation.screens.configuration.ad.asociaciones

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
import com.example.turismomovile.data.remote.dto.configuracion.Imagenes
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociaciones
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.toCreateDTO
import com.example.turismomovile.presentation.components.AppDialog
import com.example.turismomovile.presentation.components.AppEmptyState
import com.example.turismomovile.presentation.components.AppPaginationControls
import com.example.turismomovile.presentation.components.NotificationHost
import com.example.turismomovile.presentation.components.SearchBarSimple
import com.example.turismomovile.presentation.components.rememberNotificationState
import com.example.turismomovile.presentation.components.showNotification
import org.koin.compose.koinInject


// Pantalla AsociacionesScreen adaptada con componentes reutilizables

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
        viewModel.loadMunicipalidadCompleta()
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
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(70.dp))

                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.loadAllAsociaciones(searchQuery = it.ifEmpty { null })
                        },
                        placeholderText = "Buscar asociaciones...",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        when {
                            state.itemsAso.isEmpty() -> {
                                AppEmptyState(title = "No se encontraron resultados",
                                    description = "No se encontro Asociacione")
                            }
                            else -> {
                                LazyColumn(
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
                                                    asociacion.id!!, asociacion_id = asociacion.id,
                                                    url_image = "", estado = true, codigo = "", created_at = null, updated_at = null
                                                )
                                                showImgDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.totalPages > 1) {
                        AppPaginationControls(
                            currentPage = state.currentPage,
                            totalPages = state.totalPages,
                            onPreviousPage = {
                                viewModel.loadAllAsociaciones((state.currentPage - 1),searchQuery)
                            },
                            onNextPage = {
                                    viewModel.loadAllAsociaciones((state.currentPage + 1),searchQuery)

                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }

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
                    val asociacionConImagenes = updatedAsociacion.copy(
                        imagenes = updatedAsociacion.imagenes?.map { img ->
                            img.copy(asociacion_id = updatedAsociacion.id)
                        }
                    )
                    viewModel.updateAsociacion(asociacionConImagenes)
                }
                selectedAsociacion = null
            }
        )
    }

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
    var nombre by remember { mutableStateOf(asociacion.nombre ?: "") }
    var lugar by remember { mutableStateOf(asociacion.lugar ?: "") }
    var descripcion by remember { mutableStateOf(asociacion.descripcion ?: "") }
    var selectedMunicipalidadId by remember { mutableStateOf(asociacion.municipalidadId ?: "") }
    var estado by remember { mutableStateOf(asociacion.estado) }

    var imagenes: MutableList<ImgAsociaciones> by remember {
        mutableStateOf(
            asociacion.imagenes?.map {
                ImgAsociaciones(
                    id = it.id,
                    url_image = it.url_image ?: "",
                    estado = it.estado ?: true,
                    codigo = it.codigo ?: "",
                    asociacion_id = asociacion.id ?: "",
                    created_at = null,
                    updated_at = null
                )
            }?.toMutableList() ?: mutableListOf()
        )
    }
    var tempImagen by remember { mutableStateOf<ImgAsociaciones?>(null) }
    var errors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var showImageDialog by remember { mutableStateOf(false) }

    AppDialog(
        title = if (asociacion.id.isNullOrBlank()) "Nueva Asociación" else "Editar Asociación",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (nombre.isBlank() || lugar.isBlank() || descripcion.isBlank() || selectedMunicipalidadId.isBlank()) {
                    errors = mapOf(
                        "nombre" to nombre.takeIf { it.isBlank() }?.let { "Requerido" },
                        "lugar" to lugar.takeIf { it.isBlank() }?.let { "Requerido" },
                        "descripcion" to descripcion.takeIf { it.isBlank() }?.let { "Requerido" },
                        "municipalidad_id" to selectedMunicipalidadId.takeIf { it.isBlank() }?.let { "Selecciona una" }
                    ).filterValues { it != null }
                    return@TextButton
                }

                onSave(
                    asociacion.copy(
                        nombre = nombre,
                        lugar = lugar,
                        descripcion = descripcion,
                        municipalidadId = selectedMunicipalidadId,
                        estado = estado,
                    )
                )
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            var expanded by remember { mutableStateOf(false) }
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true } // Ahora todo el box es clickeable
            ) {
                OutlinedTextField(
                    value = municipalidades.firstOrNull { it.id == selectedMunicipalidadId }?.distrito ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Municipalidad") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errors.containsKey("municipalidad_id"),
                    supportingText = {
                        errors["municipalidad_id"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }
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
                                errors = errors - "municipalidad_id"
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    if (it.isNotBlank()) errors = errors - "nombre"
                },
                label = { Text("Nombre *") },
                isError = errors.containsKey("nombre"),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lugar,
                onValueChange = {
                    lugar = it
                    if (it.isNotBlank()) errors = errors - "lugar"
                },
                label = { Text("Lugar *") },
                isError = errors.containsKey("lugar"),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    if (it.isNotBlank()) errors = errors - "descripcion"
                },
                label = { Text("Descripción *") },
                isError = errors.containsKey("descripcion"),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // ESTADO
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado:", Modifier.padding(end = 8.dp))
                Switch(checked = estado, onCheckedChange = { estado = it })
                Text(if (estado) "Activo" else "Inactivo")
            }

            // IMÁGENES
            Text("Imágenes:", style = MaterialTheme.typography.labelLarge)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                imagenes.forEachIndexed { index, img ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• ${img.codigo ?: "Sin código"}", Modifier.weight(1f))
                        IconButton(onClick = {
                            imagenes.removeAt(index)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar imagen", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Button(onClick = {
                    tempImagen = null
                    showImageDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Agregar Imagen")
                }
            }
        }
    }

    if (showImageDialog) {
        ImgAsociacionDialog(
            imgAsociacion = tempImagen, // Aquí asegúrate que tempImagen sea de tipo ImgAsociaciones
            onDismiss = { showImageDialog = false },
            onSave = { nuevaImg ->
                imagenes = imagenes.toMutableList().apply {
                    val imgConAsocId = if (asociacion.id != null) {
                        nuevaImg.copy(asociacion_id = asociacion.id)
                    } else {
                        nuevaImg.copy(asociacion_id = "") // o null, según tu modelo
                    }
                    add(imgConAsocId)
                }
                showImageDialog = false
            }
        )
    }
}


@Composable
fun ImgAsociacionDialog(
    imgAsociacion: ImgAsociaciones?, // CAMBIO
    onDismiss: () -> Unit,
    onSave: (ImgAsociaciones) -> Unit // ✅ TAMBIÉN ESTO
) {
    var codigo by remember { mutableStateOf(imgAsociacion?.codigo ?: "") }
    var urlImage by remember { mutableStateOf(imgAsociacion?.url_image ?: "") }
    var estado by remember { mutableStateOf(imgAsociacion?.estado ?: true) }
    val id = imgAsociacion?.id
    val asociacionId = imgAsociacion?.asociacion_id // NECESARIO PARA CREACIÓN Y UPDATE

    val camposValidos = codigo.isNotBlank() && urlImage.isNotBlank()

    AppDialog(
        title = if (id.isNullOrBlank()) "Nueva Imagen" else "Editar Imagen",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        ImgAsociaciones(
                            id = id,
                            asociacion_id = asociacionId ?: "", // si es creación, ya lo habías seteado antes
                            codigo = codigo,
                            url_image = urlImage,
                            estado = estado,
                            created_at = imgAsociacion?.created_at,
                            updated_at = imgAsociacion?.updated_at
                        )
                    )
                },
                enabled = camposValidos
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
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
                label = { Text("Código *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = urlImage,
                onValueChange = { urlImage = it },
                label = { Text("URL de la imagen *") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado:", Modifier.padding(end = 8.dp))
                Switch(checked = estado, onCheckedChange = { estado = it })
                Text(if (estado) "Activo" else "Inactivo")
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
