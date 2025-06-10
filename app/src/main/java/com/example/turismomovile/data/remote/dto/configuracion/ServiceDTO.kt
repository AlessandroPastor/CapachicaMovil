package com.example.turismomovile.data.remote.dto.configuracion


import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.serialization.Serializable


@Serializable
data class ServiceResponse(
    val content: List<Service>,
    val currentPage: Int,
    val totalElements: Int,
    val totalPages: Int
)

@Serializable
data class Service(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val category: String,
    val status: Int,
    val emprendedores: List<Emprendedor>,
    val images: List<ServiceImage>
)

@Serializable
data class Emprendedor(
    val id: String,
    val razon_social: String,
    val address: String
)

@Serializable
data class ServiceCreateDto(
    val name: String,
    val code: String,
    val description: String,
    val category: String,
    val status: Boolean = true,
)

@Serializable
data class ServiceImage(
    val id: String,
    val imagen_url: String,
    val description: String,
    val code: String
)

data class ServiceState(
    val items: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Service? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)

