package com.example.turismomovile.data.remote.dto.configuracion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.turismomovile.presentation.components.NotificationState


@Serializable
data class ImgAsociacionesResponse(
    @SerialName("totalpages") val totalPages: Int,
    @SerialName("currentpage") val currentPage: Int,
    @SerialName("content") val content: List<ImgAsociaciones>, // O la clase correspondiente
    @SerialName("totalElements") val totalElements: Int
)

@Serializable
data class ImgAsociacionesByAsoacionesResponse(
    @SerialName("totalpages") val totalPages: Int,
    @SerialName("currentpage") val currentPage: Int,
    @SerialName("content") val content: List<ImgAsociaciones>,
    @SerialName("totalElements") val totalElements: Int
)


@Serializable
data class ImgAsociaciones(
    val id: String? = null,
    val asociacion_id : String?= null,
    val url_image: String? = null,
    val estado: Boolean = true,
    val codigo: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,

)

@Serializable
data class ImgAsociacionesCreateDTO(
    val asociacion_id: String,
    val url_image: String,
    val estado: Boolean,
    val codigo: String,
)

data class ImgAsoacionesState(
    val items: List<ImgAsociaciones> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: ImgAsociaciones? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)