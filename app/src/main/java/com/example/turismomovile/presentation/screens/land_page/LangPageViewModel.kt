package com.example.turismomovile.presentation.screens.land_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.api.configuracion.ServiceApiService
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionState
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsoacionesState
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionState
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadState
import com.example.turismomovile.data.remote.dto.configuracion.ServiceState
import com.example.turismomovile.data.remote.dto.configuracion.SliderMuni
import com.example.turismomovile.domain.repository.configuration.AsociacionesRepository
import com.example.turismomovile.domain.repository.configuration.ImgAsociacionesRepository
import com.example.turismomovile.domain.repository.configuration.MunicipalidadRepository
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LangPageViewModel (
    private val repository: MunicipalidadRepository,
    private val repositoryAso: AsociacionesRepository,
    private val repositoryImgAso: ImgAsociacionesRepository,
    private val apiserviceService : ServiceApiService
) : ViewModel() {

    private val _state = MutableStateFlow(MunicipalidadState())
    val state = _state.asStateFlow()

    private val _stateAso = MutableStateFlow(AsociacionState())
    val stateAso = _stateAso.asStateFlow()

    private val _stateImgAso = MutableStateFlow(ImgAsoacionesState())
    val stateImgAso = _state.asStateFlow()

    private val _municipalidadDescriptionState = MutableStateFlow<MunicipalidadDescriptionState>(MunicipalidadDescriptionState())
    val municipalidadDescriptionState = _municipalidadDescriptionState.asStateFlow()

    // Estado para los servicios turísticos
    private val _stateService = MutableStateFlow(ServiceState())
    val stateService = _stateService.asStateFlow()

    // **Nuevo state para las imágenes de los sliders**
    private val _sliderImagesState = MutableStateFlow<List<SliderMuni>>(emptyList())
    val sliderImagesState = _sliderImagesState.asStateFlow()

    init {
        loadMunicipalidad()
        loadAsociaciones()
        loadImgAsoaciones()
        loadService()
        loadMunicipalidadDescription()
    }

    fun loadMunicipalidad(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getMunicipalidad(page = page, name = searchQuery)
                    .onSuccess { response ->
                        _state.value = _state.value.copy(
                            items = response.content,
                            currentPage = response.currentPage,
                            totalPages = response.totalPages,
                            isLoading = false,
                            error = null
                        )

                        // Actualizamos el estado de las imágenes de los sliders
                        val sliderImages = response.content.flatMap { it.sliders ?: emptyList() }
                        _sliderImagesState.value = sliderImages
                    }
                    .onFailure { error ->
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

    // Función para cargar las descripciones de la municipalidad
    fun loadMunicipalidadDescription(page: Int = 0, size: Int = 10, searchQuery: String? = null) {
        viewModelScope.launch {
            // Establecemos que la carga está en proceso
            _municipalidadDescriptionState.value = _municipalidadDescriptionState.value.copy(isLoading = true)

            try {
                // Llamamos al repositorio para obtener las descripciones de la municipalidad
                val response = repository.getMunicipalidadDescription(page, size, searchQuery)
                response.onSuccess { municipalidadDescriptionResponse ->

                    // Debug: Mostramos la información de la respuesta
                    println("🛰️ MUNICIPALIDAD DESCRIPTION DEBUG INFO:")
                    println("📦 Total descripciones de la municipalidad en esta página: ${municipalidadDescriptionResponse.content.size}")
                    municipalidadDescriptionResponse.content.forEach { description ->
                        println("➡️ ID: ${description.id} | Título: ${description.anio_gestion}")
                    }

                    // Actualizamos el estado con los datos obtenidos
                    _municipalidadDescriptionState.value = _municipalidadDescriptionState.value.copy(
                        descriptions = municipalidadDescriptionResponse.content, // Asignamos el contenido de las descripciones
                        currentPage = municipalidadDescriptionResponse.currentPage,
                        totalPages = municipalidadDescriptionResponse.totalPages,
                        totalElements = municipalidadDescriptionResponse.totalElements,
                        isLoading = false,
                        error = null
                    )
                }.onFailure { error ->
                    // En caso de error, actualizamos el estado con el mensaje de error
                    _municipalidadDescriptionState.value = _municipalidadDescriptionState.value.copy(
                        isLoading = false,
                        error = error.message,
                        notification = NotificationState(
                            message = error.message ?: "Error al cargar las descripciones de la municipalidad",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            } catch (e: Exception) {
                // En caso de una excepción, actualizamos el estado con el mensaje de error
                _municipalidadDescriptionState.value = _municipalidadDescriptionState.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error inesperado al cargar las descripciones",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



    fun loadService(page: Int = 0, searchQuery: String? = null, category: String? = null) {
        viewModelScope.launch {
            _stateService.value = _stateService.value.copy(isLoading = true)

            try {
                val response = apiserviceService.getService(
                    page = page,
                    name = searchQuery,
                )

                println("🛰️ [API Service] Página actual: ${response.currentPage + 1} / ${response.totalPages}")
                println("📦 Total Servicios en esta página: ${response.content.size}")
                response.content.forEach { service ->
                    println("   ➡️ ID: ${service.id} | Nombre: ${service.name} | Categoría: ${service.images}")
                }

                _stateService.value = _stateService.value.copy(
                    items = response.content,
                    currentPage = response.currentPage,
                    totalPages = response.totalPages,
                    totalElements = response.totalElements,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                println("❌ [API Service] Error al cargar servicios: ${e.message}")
                _stateService.value = _stateService.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar servicios",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }




    fun loadAsociaciones(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repositoryAso.getAsociaciones(page = page, name = searchQuery)
                    .onSuccess { response ->

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ Asoaciones DEBUG INFO:")
                        println("   📄 Página actual: ${response.currentPage + 1} / ${response.totalPages}")
                        println("   📦 Total Asociacones esta página: ${response.content.size}")
                        println("   🆔 IDs de Asoaciones:")
                        response.content.forEach { asoaciones ->
                            println("     ➡️ ID: ${asoaciones.id} | Nombre: ${asoaciones.nombre}")
                        }
                        println("------------------------------------------------------------")

                        _stateAso.value = _stateAso.value.copy(
                            itemsAso = response.content,
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

    fun loadImgAsoaciones(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                println("🔄 Iniciando solicitud para cargar imágenes de asociaciones...")
                println("   📄 Parámetros de solicitud: Página = $page, Búsqueda = $searchQuery")

                repositoryImgAso.getImgAsoaciones(page = page, name = searchQuery)
                    .onSuccess { response ->

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ Respuesta de la API recibida:")
                        println("   📄 Página actual: ${response.currentPage + 1} / ${response.totalPages}")
                        println("   📦 Total imágenes de asociaciones en esta página: ${response.content.size}")
                        println("   🆔 IDs de asociaciones:")
                        response.content.forEachIndexed { index, imgAsociacion ->
                            println("     ➡️ ID: ${imgAsociacion.id} | Código: ${imgAsociacion.codigo} | Estado: ${imgAsociacion.estado} | URL Imagen: ${imgAsociacion.url_image}")
                        }
                        println("------------------------------------------------------------")

                        // Actualizar estado con los datos de la respuesta
                        _stateImgAso.value = _stateImgAso.value.copy(
                            items = response.content,
                            currentPage = response.currentPage,
                            totalPages = response.totalPages,
                            isLoading = false,
                            error = null
                        )

                        // Confirmar que los datos fueron procesados correctamente
                        println("✔️ Datos de asociaciones cargados correctamente.")
                    }
                    .onFailure { error ->
                        println("❌ Error al intentar obtener las imágenes de asociaciones.")
                        println("   📩 Detalles del error: ${error.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message,
                            notification = NotificationState(
                                message = error.message ?: "Error al cargar las imágenes de asociaciones",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            } catch (e: Exception) {
                println("❌ Excepción inesperada mientras se realizaba la solicitud:")
                println("   📩 Detalles de la excepción: ${e.message}")
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

    // Función para cargar las imágenes por asociación (añadida)
    fun loadImgAsoacionesByAsociaciones(asociacionId: String, page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _stateImgAso.value = _stateImgAso.value.copy(isLoading = true)
            try {
                println("🔄 Iniciando solicitud para cargar imágenes de asociaciones por asociación...")
                println("   📄 Parámetros de solicitud: Página = $page, Búsqueda = $searchQuery, Asociación ID = $asociacionId")

                repositoryImgAso.getImgAsoacionesByAsoaciones(asociacionId = asociacionId, page = page, name = searchQuery)
                    .onSuccess { response ->

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ Respuesta de la API recibida:")
                        println("   📄 Página actual: ${response.currentPage + 1} / ${response.totalPages}")
                        println("   📦 Total imágenes de asociaciones en esta página: ${response.content.size}")
                        println("   🆔 IDs de asociaciones:")
                        response.content.forEachIndexed { index, imgAsociacion ->
                            println("     ➡️ ID: ${imgAsociacion.id} | Código: ${imgAsociacion.codigo} | Estado: ${imgAsociacion.estado} | URL Imagen: ${imgAsociacion.url_image}")
                        }
                        println("------------------------------------------------------------")

                        // Actualizar estado con los datos de la respuesta
                        _stateImgAso.value = _stateImgAso.value.copy(
                            items = response.content,
                            currentPage = response.currentPage,
                            totalPages = response.totalPages,
                            isLoading = false,
                            error = null
                        )

                        // Confirmar que los datos fueron procesados correctamente
                        println("✔️ Datos de asociaciones cargados correctamente.")
                    }
                    .onFailure { error ->
                        println("❌ Error al intentar obtener las imágenes de asociaciones.")
                        println("   📩 Detalles del error: ${error.message}")
                        _stateImgAso.value = _stateImgAso.value.copy(
                            isLoading = false,
                            error = error.message,
                            notification = NotificationState(
                                message = error.message ?: "Error al cargar las imágenes de asociaciones",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            } catch (e: Exception) {
                println("❌ Excepción inesperada mientras se realizaba la solicitud:")
                println("   📩 Detalles de la excepción: ${e.message}")
                _stateImgAso.value = _stateImgAso.value.copy(
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



    fun refreshMunicipalidades() {
        // Establecemos el estado de "isRefreshing" en true
        _state.value = _state.value.copy(isLoading = true)
        // Recargamos los datos (puedes hacer esto de forma directa o específica si es necesario)
        loadMunicipalidad()
    }

}