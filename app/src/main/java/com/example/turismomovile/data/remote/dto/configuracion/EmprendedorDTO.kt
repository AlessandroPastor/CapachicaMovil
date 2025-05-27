package com.example.turismomovile.data.remote.dto.configuracion

import kotlinx.serialization.Serializable
import com.example.turismomovile.presentation.components.NotificationState

@Serializable
data class EmprendedorResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<Emprededor>, val totalElements: Int,
    val perPage : Int
)

@Serializable
data class Emprededor(
    val id: String? = null,
    val razonSocial : String?,
    val asociacionId: String? = null,
    val nombre_asociacion : String?,
    val newColumn: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)


@Serializable
data class EmprendedorCreateDTO(
    val asociacion_id: String,
    val razon_social: String,
)

data class EmprendedorState(
    val items: List<Emprededor> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Emprededor? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)