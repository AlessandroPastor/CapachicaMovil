package com.example.turismomovile.data.remote.dto.configuracion

import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MunicipalidadResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<Municipalidad>,
    val totalElements: Int
)

@Serializable
data class Municipalidad(
    val id: String? = null,
    val distrito : String?,
    val provincia: String?,
    val region : String?,
    val codigo : String?,
    val sliders: List<SliderMuni>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)
@Serializable
data class MunicipalidadDescriptionResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<MunicipalidadDescription>,
    val totalElements: Int
)

@Serializable
data class MunicipalidadDescription(
    val id: String? = null,
    val municipalidad_id : String?,
    val logo: String?,
    val direccion : String?,
    val descripcion : String?,
    val ruc : String?,
    val correo : String?,
    val nombre_alcalde : String?,
    val anio_gestion : String?,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null
)


@Serializable
data class SliderMuni(
    val id: String? = null,
    val titulo : String?,
    val descripcion: String?,
    val url_images : String?,
)

@Serializable
data class MunicipalidadCreateDTO(
    val distrito: String,
    val provincia: String,
    val region: String,
    val codigo: String
)

data class MunicipalidadState(
    val items: List<Municipalidad> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Role? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)

fun String.formatDateTime(): String {
    return try {
        val instant = Instant.parse(this)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"
    } catch (e: Exception) {
        this
    }
}
