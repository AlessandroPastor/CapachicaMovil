package com.example.turismomovile.data.remote.dto.ventas

import com.example.turismomovile.data.remote.dto.configuracion.Imagen
import kotlinx.serialization.Serializable

// ******************************* PARA X COSAS *************************
// SERVICIOS
@Serializable
data class AsignarServiciosRequest(
    val service_id: List<String>,
    val cantidad: List<Int>? = null,
    val costo: List<Double>? = null,
    val costo_unidad: List<Double>? = null,
    val name: List<String>? = null,
    val description: List<String>? = null
)

@Serializable
data class AsignarServiciosResponse(
    val message: String,
    val emprendedor_id: String,
    val assigned_services: List<EmprendedorByUserResponse>
)

@Serializable
data class EmprendedorByUserResponse(
    val id: String,
    val razonSocial: String,
    val asociacionId: String? = null,
    val nombre_asociacion: String? = null,
    val imagenes: List<Imagen> = emptyList(),
    val services: List<ServiceProducto> = emptyList()
)

@Serializable
data class ServiceProducto(
    val id: String,
    val name_service: String? = null,
    val description_service: String? = null,
    val code: String? = null,
    val status: Boolean? = null,
    val cantidad: Int? = null,
    val costo: Double? = null,
    val costo_unidad: Double? = null,
    val name_service_empredimiento: String? = null,
    val description_service_empredimiento: String? = null
)

@Serializable
data class ReporteVentasResponse(
    val message: String,
    val emprendedorNombre: String,
    val nombre_familia: String? = null,
    val content: List<VentaItem>,
    val totalElements: Int,
    val currentPage: Int,
    val totalPages: Int
)

@Serializable
data class VentaItem(
    val id: String,
    val code: String,
    val IGV: Double,
    val BI: Double,
    val total: Double,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val reserva: ReservaVenta? = null,
    val payment: PaymentVenta? = null,
    val detalles: List<DetalleVenta>
)

@Serializable
data class ReservaVenta(
    val id: String? = null,
    val code: String? = null,
    val status: Boolean? = null
)

@Serializable
data class PaymentVenta(
    val id: String? = null,
    val code: String? = null,
    val total: Double? = null
)

@Serializable
data class DetalleVenta(
    val id: String,
    val description: String,
    val costo: Double,
    val IGV: Double,
    val BI: Double,
    val total: Double,
    val lugar: String? = null,
    val emprendimiento_service: EmprendimientoServiceDetalle
)

@Serializable
data class EmprendimientoServiceDetalle(
    val id: String? = null,
    val name: String? = null,
    val service: ServiceDetalle? = null
)

@Serializable
data class ServiceDetalle(
    val id: String? = null,
    val name: String? = null
)
@Serializable
data class ReservasPorEmprendedorResponse(
    val emprendedor_id: String,
    val razon_social: String,
    val reservas: List<ReservaAgrupada>
)

@Serializable
data class ReservaAgrupada(
    val emprendimiento_service: EmprendimientoServiceReserva,
    val emprendimiento_service_detalle: List<ReservaDetalle>
)

@Serializable
data class EmprendimientoServiceReserva(
    val id: String,
    val name: String,
    val code: String,
    val costo: Double
)

@Serializable
data class ReservaDetalle(
    val id: String,
    val cantidad: Int,
    val lugar: String? = null,
    val description: String? = null,
    val reserva: ReservaInfo? = null
)

@Serializable
data class ReservaInfo(
    val id: String? = null,
    val code: String? = null,
    val status: Boolean? = null,
    val total: Double? = null
)