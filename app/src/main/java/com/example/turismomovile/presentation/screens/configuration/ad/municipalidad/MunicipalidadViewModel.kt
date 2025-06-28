package com.example.turismomovile.presentation.screens.configuration.ad.municipalidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadState
import com.example.turismomovile.domain.repository.configuration.MunicipalidadRepository
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MunicipalidadViewModel (
    private val repository: MunicipalidadRepository,
    ) : ViewModel() {

    private val _state = MutableStateFlow(MunicipalidadState())
    val state = _state.asStateFlow()

    init {
        loadMunicipalidad()
    }

    fun loadMunicipalidad(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getMunicipalidad(page = page, name = searchQuery)
                    .onSuccess { response ->

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ MUNICIPALIDAD DEBUG INFO:")
                        println("   📄 Página actual: ${response.currentPage} / ${response.totalPages}")
                        println("   📦 Total Municipalidades esta página: ${response.content.size}")
                        println("   🆔 IDs de Municipalidades:")
                        response.content.forEach { municipalidad ->
                            println("     ➡️ ID: ${municipalidad.id} | Nombre: ${municipalidad.distrito}")
                        }
                        println("------------------------------------------------------------")

                        _state.value = _state.value.copy(
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

    fun createMunicipalidad(dto: MunicipalidadCreateDTO) {
        viewModelScope.launch {
            println("📤 [CREATE] Intentando crear municipalidad...")
            println("   ➡️ Distrito: ${dto.distrito}")
            println("   ➡️ Provincia: ${dto.provincia}")
            println("   ➡️ Región: ${dto.region}")
            println("   ➡️ Código: ${dto.codigo}")

            _state.update { it.copy(isLoading = true) }

            repository.createMunicipalidad(dto)
                .onSuccess {
                    println("✅ [CREATE] Municipalidad creada correctamente")
                    loadMunicipalidad()
                    _state.update {
                        it.copy(
                            selectedItem = null,
                            isDialogOpen = false,
                            notification = NotificationState(
                                message = "Municipalidad creada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                }
                .onFailure { error ->
                    println("❌ [CREATE] Error al crear municipalidad: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al crear municipalidad",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
                }
        }
    }


    fun updateMunicipalidad(municipalidad: Municipalidad) {
        viewModelScope.launch {
            println("🔄 Intentando actualizar municipalidad con ID=${municipalidad.id}")
            _state.value = _state.value.copy(isLoading = true)
            municipalidad.id?.let {
                repository.updateMunicipalidad(it, municipalidad)
                    .onSuccess {
                        println("✅ Municipalidad actualizada correctamente: ID=${municipalidad.id}")
                        loadMunicipalidad()
                        _state.value = _state.value.copy(
                            isDialogOpen = false,
                            selectedItem = null,
                            notification = NotificationState(
                                message = "Municipalidad actualizada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                    .onFailure { error ->
                        println("❌ Error al actualizar municipalidad ID=${municipalidad.id}: ${error.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al actualizar municipalidad",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            }
        }
    }
}