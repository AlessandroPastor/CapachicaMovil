package com.example.turismomovile.data.remote.dto.configuracion

import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.serialization.Serializable

// Respuesta paginada general
@Serializable
data class EmprendedorServiceResponse(
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Int,
    val content: List<EmprendedorService>,
)

// Modelo completo de Emprendedor
@Serializable
data class EmprendedorService(
    val id: String? = null,
    val service_id: String? = null,
    val emprendedor_id: String? = null,
    val cantidad: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val costo: Double? = null,
    val costo_unidad: Double? = null,
    val code: String? = null,
    val status: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null,
    val emprendedor: List<Imagen> = emptyList(),
    val service: List<Producto> = emptyList(),
    val img_emprendedor_services: List<ImagenProductoService> = emptyList()
)

// Imágenes del Emprendedor
@Serializable
data class ImagenProductoService(
    val id: String? = null,
    val emprendedor_service_id: String? = null,
    val url_image: String? = null,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val code: String? = null
)

// Imágenes del Emprendedor
@Serializable
data class ServiceEmprendedorService(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val code: String? = null,
    val status: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
)



// Estado para el ViewModel (para Jetpack Compose)
data class EmprendedorServiceState(
    val items: List<EmprendedorService> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: EmprendedorService? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)