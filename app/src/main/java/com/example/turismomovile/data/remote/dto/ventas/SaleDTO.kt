package com.example.turismomovile.data.remote.dto.ventas

import kotlinx.serialization.Serializable

@Serializable
data class SaleCreateDTO(
    val reserva_id: String,
    val payment_id: String
)

@Serializable
data class SaleResponse(
    val message: String? = null,
    val ventas: List<VentasResponse>? = null
)

@Serializable
data class VentasResponse(
    val id: String? = null,
    val emprendedor_id: String? = null,
    val payment_id: String? = null,
    val reserva_id: String? = null,
    val code: String? = null,
    val BI: String? = null,
    val IGV: String? = null,
    val total: String? = null,
    val updated_at: String? = null,
    val created_at: String? = null,
)