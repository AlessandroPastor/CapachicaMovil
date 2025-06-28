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
    val url : String? = null,
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
    val asociacion_id: String? = null, // <-- AGREGADO
    val url_image: String? = null,
    val estado: Boolean? = null,
    val codigo: String? = null,
    val description: String? = null
)

@Serializable
data class AsociacionCreateDTO(
    val municipalidad_id: String? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val lugar: String? = null,
    val phone: String? = null,
    val office_hours: String? = null,
    val url: String? = null,
    val estado: Boolean? = null,
    val imagenes: List<Imagenes>? = null // En crear, asociacion_id y id pueden ser null
)

@Serializable
data class AsociacionUpdateDTO(
    val municipalidad_id: String,
    val nombre: String,
    val descripcion: String,
    val lugar: String,
    val phone: String,
    val office_hours: String,
    val url: String,
    val estado: Boolean,
    val imagenes: List<ImagenUpdateDTO>
)

@Serializable
data class ImagenUpdateDTO(
    val id: String?, // Puede ser null para nuevas imágenes
    val asociacion_id: String, // Siempre debe ir
    val url_image: String,
    val estado: Boolean,
    val codigo: String,
    val description: String
)

fun Asociacion.toCreateDTO(): AsociacionCreateDTO? {
    return if (nombre != null && lugar != null && descripcion != null && municipalidadId != null) {
        AsociacionCreateDTO(
            municipalidad_id = municipalidadId,
            nombre = nombre,
            descripcion = descripcion,
            lugar = lugar,
            phone = phone,
            office_hours = office_hours,
            url = url,
            estado = estado,
            imagenes = imagenes // En crear, asociacion_id e id pueden ser null
        )
    } else null
}

// NUEVO: Extension para update DTO
fun Asociacion.toUpdateDTO(): AsociacionUpdateDTO? {
    return if (
        id != null && nombre != null && lugar != null && descripcion != null && municipalidadId != null && imagenes != null
    ) {
        AsociacionUpdateDTO(
            municipalidad_id = municipalidadId,
            nombre = nombre,
            descripcion = descripcion,
            lugar = lugar,
            phone = phone ?: "",
            office_hours = office_hours ?: "",
            url = url ?: "",
            estado = estado,
            imagenes = imagenes.map {
                ImagenUpdateDTO(
                    id = it.id,
                    asociacion_id = id, // Siempre va el id de la asociacion
                    url_image = it.url_image ?: "",
                    estado = it.estado ?: true,
                    codigo = it.codigo ?: "",
                    description = it.description ?: ""
                )
            }
        )
    } else null
}




// Extensiones para ImgAsociaciones (modelo para UI/compose)
fun ImgAsociaciones.toCreateDTO(): ImgAsociacionesCreateDTO? {
    return if (codigo != null && url_image != null && asociacion_id != null) {
        ImgAsociacionesCreateDTO(
            codigo = codigo!!,
            estado = estado ?: true,
            url_image = url_image!!,
            asociacion_id = asociacion_id!!
        )
    } else {
        null
    }
}

// PARA OTRAS COSAS
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




