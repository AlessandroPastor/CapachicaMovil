package com.example.turismomovile.data.remote.dto.ventas

import com.example.turismomovile.presentation.components.NotificationState
import kotlinx.serialization.Serializable

@Serializable
data class PaymentCreateDTO(
    val reserva_id: String
)

@Serializable
data class PaymentResponse(
    val message: String? = null,
    val payment: Payments? = null
)

@Serializable
data class Payments(
    val id: String? = null,
    val code: String? = null,
    val total: String? = null,
    val igv: String? = null,
    val reserva_id: String? = null,
    val updated_at: String? = null,
    val created_at: String? = null
)
data class PaymentState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    val notification: NotificationState = NotificationState(),
    val payment: Payments? = null
)