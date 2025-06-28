package com.example.turismomovile.presentation.screens.configuration.ad.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.api.configuracion.ServiceApiService
import com.example.turismomovile.data.remote.dto.configuracion.ServiceCreateDto
import com.example.turismomovile.data.remote.dto.configuracion.ServiceState
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceViewModel (
    private val apiservice: ServiceApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceState())
    val state = _state.asStateFlow()

    init {
        loadService()
    }

    fun loadService(page: Int? = 0, search: String? = null, category: String? = null) {
        viewModelScope.launch {
            // Iniciamos el estado de carga
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Realizamos la solicitud de servicios con los filtros pasados
                val response = apiservice.getService(
                    page = page,
                    search = search,
                    category = category
                )

                // Actualizamos el estado con los resultados de la respuesta
                _state.value = _state.value.copy(
                    items = response.content,  // Asignamos los servicios recibidos
                    currentPage = response.currentPage,  // Página actual
                    totalPages = response.totalPages,  // Total de páginas
                    totalElements = response.totalElements,  // Total de elementos
                    isLoading = false,  // Finalizamos el estado de carga
                    error = null  // Limpiamos cualquier error previo
                )

            } catch (e: Exception) {
                // En caso de error, mostramos el mensaje y actualizamos el estado
                println("❌ [API Service] Error al cargar servicios: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,  // Finalizamos el estado de carga
                    error = e.message,  // Asignamos el mensaje de error
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar servicios",  // Notificación de error
                        type = NotificationType.ERROR,
                        isVisible = true  // Mostramos la notificación
                    )
                )
            }
        }
    }
    fun createService(dto: ServiceCreateDto) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val newService = apiservice.createService(dto)
                println("✅ [CREATE] Servicio creado: ${newService.name}")
                loadService() // recarga
                _state.value = _state.value.copy(
                    notification = NotificationState(
                        message = "Servicio creado exitosamente",
                        type = NotificationType.SUCCESS,
                        isVisible = true
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                println("❌ [CREATE] Error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al crear servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun updateService(id: String, service: com.example.turismomovile.data.remote.dto.configuracion.Service) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val updated = apiservice.updateService(id, service)
                println("✅ [UPDATE] Servicio actualizado: ${updated.name}")
                loadService()
                _state.value = _state.value.copy(
                    notification = NotificationState(
                        message = "Servicio actualizado exitosamente",
                        type = NotificationType.SUCCESS,
                        isVisible = true
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                println("❌ [UPDATE] Error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al actualizar servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun deleteService(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                apiservice.deleteService(id)
                println("🗑️ [DELETE] Servicio eliminado ID=$id")
                loadService()
                _state.value = _state.value.copy(
                    notification = NotificationState(
                        message = "Servicio eliminado correctamente",
                        type = NotificationType.SUCCESS,
                        isVisible = true
                    ),
                    isLoading = false
                )
            } catch (e: Exception) {
                println("❌ [DELETE] Error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al eliminar servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun getServiceById(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val service = apiservice.getServiceById(id)
                println("🔍 [GET BY ID] Servicio: ${service.name}")
                _state.value = _state.value.copy(
                    selectedItem = service,
                    isLoading = false
                )
            } catch (e: Exception) {
                println("❌ [GET BY ID] Error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    notification = NotificationState(
                        message = e.message ?: "Error al obtener servicio",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }



}