package com.example.turismomovile.presentation.screens.configuration.role.asociaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionState
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsoacionesState
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociaciones
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadState
import com.example.turismomovile.domain.repository.configuration.AsociacionesRepository
import com.example.turismomovile.domain.repository.configuration.ImgAsociacionesRepository
import com.example.turismomovile.domain.repository.configuration.MunicipalidadRepository
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AsociacionesViewModel (
    private val repository: AsociacionesRepository,
    private val repositoryMuni: MunicipalidadRepository,
    private val repositoryImgAso: ImgAsociacionesRepository,
    ) : ViewModel() {

    // Versión corregida
    private val _state = MutableStateFlow(AsociacionState())
    val state = _state.asStateFlow()

    private val _stateMuni = MutableStateFlow(MunicipalidadState())
    val stateMuni = _stateMuni.asStateFlow()

    private val _stateImgAso = MutableStateFlow(ImgAsoacionesState())
    val stateImgAso = _stateImgAso.asStateFlow()

    init {
        loadAllAsociaciones()
        loadAllImgAsoaciones()
        loadMunicipalidad()
    }

    fun loadAllAsociaciones(searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, currentPage = 0)  // Inicializamos currentPage en 0
            try {
                println("🔄 Iniciando solicitud para cargar todas las asociaciones...")
                println("   📄 Parámetros de búsqueda: $searchQuery")

                var currentPage = 0  // Aseguramos que currentPage sea 0
                var totalPages = 1   // Inicializamos totalPages con un valor por defecto
                var allContent: List<Asociacion> = emptyList()

                do {
                    println("🔄 Cargando página $currentPage...")
                    val response = repository.getAsociaciones(page = currentPage, name = searchQuery)

                    response.onSuccess { res ->
                        totalPages = res.totalPages // Asignamos el valor real de totalPages
                        val content = res.content

                        // Concatenar las asociaciones de la página actual
                        allContent = allContent + content

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ Respuesta de la API recibida para la página $currentPage:")
                        println("   📦 Total asociaciones en esta página: ${content.size}")
                        println("   🆔 IDs de asociaciones: ${content.map { it.id }}")
                        println("------------------------------------------------------------")
                    }.onFailure { error ->
                        throw error
                    }

                    // Incrementar la página para la siguiente solicitud
                    currentPage++

                } while (currentPage < totalPages)

                // Actualizar estado con todas las asociaciones
                _state.value = _state.value.copy(
                    itemsAso = allContent,
                    currentPage = currentPage,  // Asegúrate de actualizar currentPage
                    totalPages = totalPages,
                    isLoading = false,
                    error = null
                )

                // Confirmar que los datos fueron procesados correctamente
                println("✔️ Todas las asociaciones cargadas correctamente.")

            } catch (e: Exception) {
                println("❌ Error al intentar obtener las asociaciones.")
                println("   📩 Detalles del error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar asociaciones",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



    fun loadMunicipalidad(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repositoryMuni.getMunicipalidad(page = page, name = searchQuery)
                    .onSuccess { response ->

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ MUNICIPALIDAD DEBUG INFO:")
                        println("   📄 Página actual: ${response.currentPage + 1} / ${response.totalPages}")
                        println("   📦 Total Municipalidades esta página: ${response.content.size}")
                        println("   🆔 IDs de Municipalidades:")
                        response.content.forEach { municipalidad ->
                            println("     ➡️ ID: ${municipalidad.id} | Nombre: ${municipalidad.distrito}")
                        }
                        println("------------------------------------------------------------")

                        _stateMuni.value = _stateMuni.value.copy(
                            items = response.content,
                            currentPage = response.currentPage,
                            totalPages = response.totalPages,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        println("❌ Error al cargar municipalidades: ${error.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message,
                            notification = NotificationState(
                                message = error.message ?: "Error al cargar municipalidades",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            } catch (e: Exception) {
                println("❌ Excepción inesperada: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error inesperado",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



    fun createAsociaciones(dto: AsociacionCreateDTO) {
        viewModelScope.launch {
            println("📤 [CREATE] Intentando crear Asociaciones...")
            println("   ➡️ Lugar: ${dto.lugar}")
            println("   ➡️ Nombre: ${dto.nombre}")
            println("   ➡️ Description: ${dto.descripcion}")
            println("   ➡️ Municipality: ${dto.municipalidad_id}")

            _state.update { it.copy(isLoading = true) }

            repository.createAsociaciones(dto)
                .onSuccess {
                    println("✅ [CREATE] Asociacion creada correctamente")
                    loadAllAsociaciones()
                    _state.update {
                        it.copy(
                            selectedItem = null,
                            isDialogOpen = false,
                            notification = NotificationState(
                                message = "Asociacion creada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                }
                .onFailure { error ->
                    println("❌ [CREATE] Error al crear Asociacion: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al crear Asociacion",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
                }
        }
    }

    fun updateAsociaciones(asociacion: Asociacion) {
        viewModelScope.launch {
            // Log de inicio de la actualización
            println("🔄 Intentando actualizar Asociación con ID=${asociacion.id}")
            println("   ➡️ Datos de la Asociación a actualizar: ${asociacion}")

            // Cambiamos el estado a cargando
            _state.value = _state.value.copy(isLoading = true)

            // Verificamos si la asociación tiene un ID válido
            asociacion.id?.let { id ->
                println("🔄 ID de la Asociación: $id")
                println("   ➡️ Intentando enviar los siguientes datos al repositorio: ${asociacion}")

                // Realizamos la actualización a través del repositorio
                repository.updateAsociaciones(id, asociacion)
                    .onSuccess { response ->
                        // Respuesta exitosa
                        println("✅ Asociación actualizada correctamente con ID=$id")
                        println("   ➡️ Respuesta del servidor: ${response.toString()}")

                        // Mostrar los datos que hemos recibido como respuesta del servidor
                        if (response != null) {
                            println("   ➡️ Datos actualizados de la asociación: ${response.nombre}, ${response.lugar}")
                        } else {
                            println("   ➡️ Respuesta vacía o no esperada del servidor.")
                        }

                        // Cargamos nuevamente las asociaciones después de la actualización
                        loadAllAsociaciones()

                        // Actualizar el estado
                        _state.value = _state.value.copy(
                            isDialogOpen = false,
                            selectedItem = null,
                            notification = NotificationState(
                                message = "Asociación actualizada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                        println("   ✅ Estado actualizado correctamente")
                    }
                    .onFailure { error ->
                        // Error al intentar actualizar la asociación
                        println("❌ Error al actualizar Asociación ID=$id: ${error.message}")
                        println("   ➡️ Detalles del error: ${error.message}")
                        println("   ➡️ Stack Trace: ${error.stackTraceToString()}")

                        // Actualizar el estado con un error
                        _state.value = _state.value.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al actualizar Asociación",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )

                        // Enviar un mensaje más específico según el tipo de error
                        if (error.message?.contains("Connection") == true) {
                            println("   ❌ Error de conexión. Verifica la red o el servidor.")
                        }
                    }
            } ?: run {
                // Si no existe un ID, mostramos un error
                println("❌ No se proporcionó un ID para la Asociación.")
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = "No se proporcionó un ID para la Asociación",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



    fun deleteImgAsociaciones(id: String) {
        viewModelScope.launch {
            println("🗑️ Intentando eliminar Imagen de Asociacion con ID=$id")
            _state.value = _state.value.copy(isLoading = true)
            repositoryImgAso.deleteImgAsoaciones(id)
                .onSuccess {
                    println("✅ Imagen de Asociacion eliminada correctamente: ID=$id")
                    loadAllAsociaciones()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Imagen de Asociacion eliminada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    println("❌ Error al eliminar Asociacion ID=$id: ${error.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar Imagen de Asociacion",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun loadAllImgAsoaciones(searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                println("🔄 Iniciando solicitud para cargar todas las imágenes de asociaciones...")
                println("   📄 Parámetros de búsqueda: $searchQuery")

                var currentPage = 0
                var totalPages = 1 // Inicializamos totalPages con un valor por defecto
                var allContent: List<ImgAsociaciones> = emptyList()

                do {
                    println("🔄 Cargando página $currentPage...")
                    val response = repositoryImgAso.getImgAsoaciones(page = currentPage, name = searchQuery)

                    response.onSuccess { res ->
                        totalPages = res.totalPages // Asignamos el valor real de totalPages
                        val content = res.content

                        // Concatenar las imágenes de la página actual
                        allContent = allContent + content

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ Respuesta de la API recibida para la página $currentPage:")
                        println("   📦 Total imágenes de asociaciones en esta página: ${content.size}")
                        println("   🆔 IDs de asociaciones: ${content.map { it.id }}")
                        println("------------------------------------------------------------")
                    }.onFailure { error ->
                        throw error
                    }

                    // Incrementar la página para la siguiente solicitud
                    currentPage++

                } while (currentPage < totalPages)

                // Actualizar estado con todas las imágenes
                _stateImgAso.value = _stateImgAso.value.copy(
                    items = allContent,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    isLoading = false,
                    error = null
                )

                // Confirmar que los datos fueron procesados correctamente
                println("✔️ Todas las imágenes de asociaciones cargadas correctamente.")

            } catch (e: Exception) {
                println("❌ Error al intentar obtener las imágenes de asociaciones.")
                println("   📩 Detalles del error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar las imágenes de asociaciones",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }


    fun createImgAsociaciones(dto: ImgAsociacionesCreateDTO) {
        viewModelScope.launch {
            println("📤 [CREATE] Intentando crear Imagenes de Asociaciones...")
            println("   ➡️ Codigo: ${dto.codigo}")
            println("   ➡️ Estado: ${dto.estado}")
            println("   ➡️ Url_Image: ${dto.url_image}")
            println("   ➡️ Association: ${dto.asociacion_id}")

            _state.update { it.copy(isLoading = true) }

            repositoryImgAso.createImgAsoaciaciones(dto)
                .onSuccess {
                    println("✅ [CREATE] Imagen creada para la asociacion creada correctamente")
                    loadAllAsociaciones()
                    _state.update {
                        it.copy(
                            selectedItem = null,
                            isDialogOpen = false,
                            notification = NotificationState(
                                message = "Imagen de asociacion creada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                }
                .onFailure { error ->
                    println("❌ [CREATE] Error al crear Imagen de Asociacion: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al crear Imagen de Asociacion",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
                }
        }
    }

    fun updateImgAsociaciones(asociaonesimg: ImgAsociaciones) {
        viewModelScope.launch {
            println("🔄 Intentando actualizar Imagen de Asociacion con ID=${asociaonesimg.id}")
            _state.value = _state.value.copy(isLoading = true)
            asociaonesimg.id?.let {
                repositoryImgAso.updateImgAsoaciones(it, asociaonesimg)
                    .onSuccess {
                        println("✅Imagen de Asociacion actualizada correctamente: ID=${asociaonesimg.id}")
                        loadAllAsociaciones()
                        _state.value = _state.value.copy(
                            isDialogOpen = false,
                            selectedItem = null,
                            notification = NotificationState(
                                message = "Asociacion actualizada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                    .onFailure { error ->
                        println("❌ Error al actualizar Imagen de Asociacion ID=${asociaonesimg.id}: ${error.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al actualizar Imagen de Asociacion",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            }
        }
    }



    fun deleteAsociaciones(id: String) {
        viewModelScope.launch {
            println("🗑️ Intentando eliminar Asociacion con ID=$id")
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteAsociaciones(id)
                .onSuccess {
                    println("✅ Asociacion eliminada correctamente: ID=$id")
                    loadAllAsociaciones()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Asociacion eliminada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    println("❌ Error al eliminar Asociacion ID=$id: ${error.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar Asociacion",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }



    fun closeDialog() {
        _state.value = _state.value.copy(
            isDialogOpen = false,
            selectedItem = null
        )
    }



}