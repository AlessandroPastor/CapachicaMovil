package com.example.turismomovile.presentation.screens.configuration.role.municipalidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.api.configuracion.MunicipalidadDescriptionApiService
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescription
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionState
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionUpdateDto
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MunicipalidadDescriptionViewModel(
    private val apiServiceMunicipalidadDescription: MunicipalidadDescriptionApiService
) : ViewModel() {

    private val _state = MutableStateFlow(MunicipalidadDescriptionState())
    val stateDescription = _state.asStateFlow()

    init {
        loadMunicipalidadDescription()
    }

    fun loadMunicipalidadDescription(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = apiServiceMunicipalidadDescription.getMunicipalidadDesc(page = page, name = searchQuery)

                println("🛰️ MUNICIPALIDAD DESCRIPTION DEBUG INFO:")
                println("   📄 Página actual: ${response.currentPage } / ${response.totalPages}")
                println("   📦 Total Municipalidades en esta página: ${response.content.size}")
                println("   🆔 IDs de Municipalidades:")
                response.content.forEach { municipalidad ->
                    println("     ➡️ ID: ${municipalidad.id} | Nombre: ${municipalidad.direccion}")
                }
                println("------------------------------------------------------------")

                _state.value = _state.value.copy(
                    descriptions = response.content,
                    currentPage = response.currentPage,
                    totalPages = response.totalPages,
                    isLoading = false,
                    error = null
                )
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

    fun updateMunicipalidadDescription(municipalidadDescription: MunicipalidadDescription) {
        viewModelScope.launch {
            println("🔄 Intentando actualizar la descripción de la municipalidad con ID=${municipalidadDescription.id}")
            _state.value = _state.value.copy(isLoading = true)

            municipalidadDescription.id?.let { id ->
                val updateDto = MunicipalidadDescriptionUpdateDto(
                    municipalidad_id = municipalidadDescription.municipalidad_id,
                    logo = municipalidadDescription.logo,
                    direccion = municipalidadDescription.direccion,
                    descripcion = municipalidadDescription.descripcion,
                    ruc = municipalidadDescription.ruc,
                    correo = municipalidadDescription.correo,
                    nombre_alcalde = municipalidadDescription.nombre_alcalde,
                    anio_gestion = municipalidadDescription.anio_gestion
                )

                try {
                    // 🔥 CAMBIO AQUÍ: Esperamos la respuesta con el objeto actualizado
                    val updatedMunicipalidad = apiServiceMunicipalidadDescription.updateMunicipalidadDescription(id, updateDto)

                    // ✅ Asumimos éxito si no hay excepción
                    println("✅ Descripción de la municipalidad actualizada correctamente: ID=${municipalidadDescription.id}")

                    // Puedes actualizar el objeto en tu UI o en el estado
                    loadMunicipalidadDescription() // Recargamos las descripciones si es necesario

                    // Actualiza el estado para indicar que la operación fue exitosa
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = "Descripción de la municipalidad actualizada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        ),
                    )

                } catch (error: Exception) {
                    println("❌ Error al actualizar la descripción de la municipalidad ID=${municipalidadDescription.id}: ${error.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al actualizar descripción de la municipalidad",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }

}
