package com.example.turismomovile.data.remote.dto.ventas

import kotlinx.serialization.Serializable

@Serializable
data class SaleCreateDTO(
    val reserva_id: String,
    val payment_id: String
)

@Serializable
data class SaleResponse(
    val message: String? = null
)