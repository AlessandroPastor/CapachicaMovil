package com.example.turismomovile.data.remote.dto.ventas
import com.example.turismomovile.data.remote.dto.configuracion.Service
import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservaListResponse(
    val content: List<ReservaUsuarioDTO>,
    val totalElements: Int,
    val currentPage: Int,
    val totalPages: Int
)

@Serializable
data class ReservaUsuarioDTO(
    val id: String,
    val user_id: Int? = null,
    val code: String? = null,
    val bi: String? = null,
    val igv: String? = null,
    val total: String? = null,
    val status: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val reserve_details: List<ReservaDetalleResponseDTO>,
    val user: UserReservaDTO? = null
)

@Serializable
data class ReservaDetalleResponseDTO(
    val id: String,
    val emprendedor_service_id: String? = null,
    val reserva_id: String? = null,
    val costo: String? = null,
    val cantidad: String? = null,
    val IGV: String? = null,
    val BI: String? = null,
    val total: String? = null,
    val lugar: String? = null,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val emprendimiento_service: EmprendimientoServiceDTO? = null
)

@Serializable
data class EmprendimientoServiceDTO(
    val id: String,
    val service_id: String? = null,
    val emprendedor_id: String? = null,
    val cantidad: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val costo: String? = null,
    val costo_unidad: String? = null,
    val code: String? = null,
    val status: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val service: ServiceDTO? = null
)

@Serializable
data class ServiceDTO(
    val id: String,
    val name: String? = null,
    val description: String? = null,
    val code: String? = null,
    val category: String? = null,
    val status: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null
)

@Serializable
data class UserReservaDTO(
    val id: Int,
    val name: String? = null,
    val last_name: String? = null,
    val code: String? = null,
    val username: String? = null,
    val email: String? = null,
    val email_verified_at: String? = null,
    val imagen_url: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
)




//  ************************** DTO PARA CREAR RESERVA ******************************
@Serializable
data class ReservaCreateDTO(
    val details: List<ReservaDetalleCreateDTO>
)

@Serializable
data class ReservaDetalleCreateDTO(
    val emprendedor_service_id: String,
    val cantidad: Int,
    val lugar: String? = null
)

@Serializable
data class ReservaDetalleResponse(
    val message: String,
    val reserva: ReservaResponse
)

@Serializable
data class ReservaResponse(
    val user_id: Int,
    val code: String,
    val total: Double,
    val bi: Double,
    val igv: Double,
    val id: String,
    val updated_at: String,
    val created_at: String,
    val reserve_details: List<ReservaDetalleDResponse>
)

@Serializable
data class ReservaDetalleDResponse(
    val id: String,
    val emprendedor_service_id: String,
    val reserva_id: String,
    val costo: String,
    val cantidad: String,
    val IGV: String,
    val BI: String,
    val total: String,
    val lugar: String,
    val description: String,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String? = null
)

data class ReservaState(
    val items: List<ReservaUsuarioDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Service? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState(),
    val lastCreatedReservaId: String? = null,
    // Nuevos estados para el proceso de reserva
    val isReservaInProgress: Boolean = false,
    val reservaSuccess: Boolean = false,
    val lastCreatedReservaCode: String? = null,
    val reservaError: String? = null
)
