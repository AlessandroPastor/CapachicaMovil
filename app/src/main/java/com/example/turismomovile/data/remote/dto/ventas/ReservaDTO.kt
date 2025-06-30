package com.example.turismomovile.data.remote.dto.ventas
import com.example.turismomovile.data.remote.dto.configuracion.Service
import com.example.turismomovile.presentation.components.NotificationState
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
    val bi: Double ? = null,
    val igv: Double ? = null,
    val total: Double ? = null,
    val status: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val reserveDetails: List<ReservaDetalleResponseDTO>,
    val user: UserReservaDTO ? = null
)

@Serializable
data class ReservaDetalleResponseDTO(
    val id: String,
    val emprendedor_service_id: String?= null,
    val reserva_id: String?= null,
    val costo: Double?= null,
    val cantidad: Int?= null,
    val IGV: Double?=  null,
    val BI: Double?= null,
    val total: Double?= null,
    val lugar: String?= null,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val deleted_at: String? = null,
    val emprendimiento_service: EmprendimientoServiceDTO? = null
)

@Serializable
data class EmprendimientoServiceDTO(
    val id: String,
    val service_id: String?= null,
    val emprendedor_id: String?= null,
    val cantidad: Int?= null,
    val name: String?= null,
    val description: String?= null,
    val costo: Double? = null,
    val costo_unidad: Double? = null,
    val code: String? = null,
    val status: Int?= null,
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
    val status: Int?= null,
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
data class ReservaDetalleCreateDTO(
    val emprendedor_service_id: String,
    val cantidad: Int,
    val lugar: String? = null
)

@Serializable
data class ReservaCreateDTO(
    val details: List<ReservaDetalleCreateDTO>
)

@Serializable
data class ReservaDetalleResponse(
    val id: String,
    val emprendedor_service_id: String,
    val reserva_id: String,
    val description: String? = null,
    val cantidad: Int,
    val costo: Double,
    val bi: Double,
    val igv: Double,
    val total: Double,
    val lugar: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class ReservaResponse(
    val id: String,
    val user_id: String,
    val code: String,
    val total: Double,
    val bi: Double,
    val igv: Double,
    val status: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val reserveDetails: List<ReservaDetalleResponse> = emptyList()
)

data class ReservaState(
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
