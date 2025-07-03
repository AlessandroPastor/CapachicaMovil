package com.example.turismomovile.presentation.screens.land_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.api.ventas.PaymentApiService
import com.example.turismomovile.data.remote.dto.ventas.PaymentCreateDTO
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    val notification: NotificationState = NotificationState()
)

class PaymentViewModel(
    private val paymentApiService: PaymentApiService
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    fun createPayment(reservaId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = paymentApiService.createPayment(PaymentCreateDTO(reservaId))
                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = response.message,
                        notification = NotificationState(
                            message = response.message ?: "Pago realizado con éxito",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                        notification = NotificationState(
                            message = e.message ?: "Error al realizar pago",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }

    fun clearSuccess() {
        _state.update { it.copy(successMessage = null) }
    }
}