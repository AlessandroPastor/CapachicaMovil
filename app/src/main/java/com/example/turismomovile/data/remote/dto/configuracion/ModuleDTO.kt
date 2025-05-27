package com.example.turismomovile.data.remote.dto.configuracion

import kotlinx.serialization.Serializable
import com.example.turismomovile.presentation.components.NotificationState

@Serializable
data class ModuleDTO(
    val id: String? = null,
    val title: String = "",
    val subtitle: String = "",
    val type: String = "",
    val code: String? = null,
    val icon: String? = null,
    val status: Boolean = true,
    val moduleOrder: Int = 0,
    val link: String = "",
    val parentModule: ParentModule? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

@Serializable
data class ModuleCreateDTO(
    val id: String? = null,
    val title: String = "",
    val subtitle: String = "",
    val type: String = "",
    val icon: String? = null,
    val status: Boolean = true,
    val selected: Boolean = true,
    val moduleOrder: Int = 0,
    val link: String = "",
    val parentModuleId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

@Serializable
data class ModuleSelectedDTO(
    val id: String? = null,
    val title: String,
    val subtitle: String? = null,
    val type: String? = null,
    val icon: String? = null,
    val status: Boolean = true,
    val moduleOrder: Int? = null,
    val link: String,
    val parentModuleId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null,
    val selected: Boolean? = null
)

@Serializable
data class ModuleResponse(
    val content: List<ModuleDTO>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Int
)


data class ModuleState(
    val items: List<ModuleDTO> = emptyList(),
    val parentModules: List<ParentModule> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: ModuleDTO? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)

// 📌 Conversión segura de ModuleSelectedDTO a ModuleDTO
fun ModuleSelectedDTO.toModuleDTO(): ModuleDTO {
    return ModuleDTO(
        id = this.id ?: "",
        title = this.title,
        subtitle = this.subtitle ?: "",
        type = this.type ?: "",
        icon = this.icon ?: "",
        status = this.status,
        moduleOrder = this.moduleOrder ?: 0,
        link = this.link,
        createdAt = this.createdAt ?: "",
        updatedAt = this.updatedAt ?: "",
        deletedAt = this.deletedAt ?: ""
    )
}

// 📌 Conversión segura de ModuleCreateDTO a ModuleDTO
fun ModuleCreateDTO.toModuleDTO(): ModuleDTO {
    return ModuleDTO(
        id = null,
        title = this.title,
        subtitle = this.subtitle.takeIf { it.isNotBlank() } ?: "",
        type = this.type.takeIf { it.isNotBlank() } ?: "",
        icon = this.icon?.takeIf { it.isNotBlank() } ?: "",
        status = this.status,
        moduleOrder = this.moduleOrder,
        link = this.link,
        parentModule = ParentModule(this.parentModuleId, "Sin título")
    )
}