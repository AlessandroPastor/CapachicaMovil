package com.example.turismomovile.data.remote.dto.configuracion

import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.serialization.Serializable

@Serializable
data class AsociacionResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<Asociacion>,
    val totalElements: Int
)

@Serializable
data class Asociacion(
    val id: String? = null,
    val nombre : String? = null,
    val descripcion: String? = null,
    val lugar : String? = null,
    val phone : String? = null,
    val office_hours : String? = null,
    val estado: Boolean = true,
    val municipalidadId: String? = null,
    val imagenes : List<Imagenes>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

@Serializable
data class Imagenes(
    val id: String? = null,
    val url_image: String?,
    val estado: Boolean?,
    val codigo: String
)


@Serializable
data class AsociacionCODE(
    val id: String? = null,
    val nombre : String?,
    val description: String?,
    val lugar : String?,
    val codigo : String?,
    val estado: String? = null,
    val municipalidadId: String? = null
)

@Serializable
data class EmprendedorCODE(
    val id: String? = null,
    val razon_social : String?,
    val familia: String?,
    val asociacion_id : String?,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)


@Serializable
data class AsociacionWithFamily(
    val asociacion: List<AsociacionCODE>,
    val emprendedores: List<EmprendedorCODE>,
)

@Serializable
data class AsociacionCreateDTO(
    val municipalidad_id: String,
    val nombre: String,
    val descripcion: String,
    val lugar: String,
    val estado : Boolean
)

data class AsociacionState(
    val itemsAso: List<Asociacion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Role? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)

// Extensiones para Asociacion
fun Asociacion.toCreateDTO(): AsociacionCreateDTO? {
    return if (nombre != null && lugar != null && descripcion != null && municipalidadId != null) {
        AsociacionCreateDTO(
            municipalidad_id = municipalidadId!!,
            nombre = nombre!!,
            descripcion = descripcion!!,
            lugar = lugar!!,
            estado = estado ?: true
        )
    } else {
        null
    }
}

fun Asociacion.empty() = Asociacion(
    id = "",
    nombre = "",
    lugar = "",
    descripcion = "",
    estado = true,
    municipalidadId = "",
    imagenes = emptyList(),
    createdAt = "",
    updatedAt = "",
    deletedAt = null
)

// Extensiones para ImgAsociaciones
fun ImgAsociaciones.toCreateDTO(): ImgAsociacionesCreateDTO? {
    return if (codigo != null && url_image != null && asociacion_id != null) {
        ImgAsociacionesCreateDTO(
            codigo = codigo!!,
            estado = estado?.toBoolean() ?: true,
            url_image = url_image!!,
            asociacion_id = asociacion_id!!
        )
    } else {
        null
    }
}

fun ImgAsociaciones.empty(asociacionId: String) = ImgAsociaciones(
    id = "",
    codigo = "",
    url_image = "",
    estado = "true",
    asociacion_id = asociacionId,
)

// Extensión para conversión de estado
fun String.toBoolean(): Boolean {
    return this == "true" || this == "1"
}