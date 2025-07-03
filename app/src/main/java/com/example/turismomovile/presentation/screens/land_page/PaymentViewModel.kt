package com.example.turismomovile.presentation.screens.land_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.api.ventas.PaymentApiService
import com.example.turismomovile.data.remote.api.ventas.ReservaApiService
import com.example.turismomovile.data.remote.api.ventas.SaleApiService
import com.example.turismomovile.data.remote.dto.ventas.PaymentCreateDTO
import com.example.turismomovile.data.remote.dto.ventas.PaymentState
import com.example.turismomovile.data.remote.dto.ventas.ReservaDetalleResponse
import com.example.turismomovile.data.remote.dto.ventas.ReservaState
import com.example.turismomovile.data.remote.dto.ventas.SaleCreateDTO
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.turismomovile.data.remote.dto.ventas.ReservaUsuarioDTO


class PaymentViewModel(
    private val paymentApiService: PaymentApiService,
    private val saleApiService: SaleApiService,
    private val reservaApiService: ReservaApiService,
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentState())
    val state = _state.asStateFlow()

    private val _reservasState = MutableStateFlow(ReservaState())
    val reservasState = _reservasState.asStateFlow()

    private val _reserva = MutableStateFlow<ReservaUsuarioDTO?>(null)
    val reserva = _reserva.asStateFlow()

    init {
        loadReservas()
    }

    fun loadReservas(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            // Mostrar el estado de carga
            _state.update { it.copy(isLoading = true) }

            // Imprime los parámetros antes de realizar la solicitud
            println("Cargando reservas: Página $page, Búsqueda: $searchQuery")

            try {
                // Realiza la llamada a la API
                val response = reservaApiService.getReservas(page = page, search = searchQuery)

                // Imprime la respuesta recibida
                println("Respuesta recibida: ${response.content.size} elementos, Página actual: ${response.currentPage}, Total de páginas: ${response.totalPages}")

                // Actualiza el estado con los resultados
                _reservasState.update {
                    it.copy(
                        items = response.content, // O mapea según tu modelo de dominio
                        currentPage = response.currentPage,
                        totalPages = response.totalPages,
                        totalElements = response.totalElements,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                // Imprime el error si ocurre una excepción
                println("Error al cargar reservas: ${e.message}")

                // Actualiza el estado con el error
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                        notification = NotificationState(
                            message = e.message ?: "Error al cargar reservas",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }
    fun loadReserva(reservaId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = reservaApiService.getReservaById(reservaId)
                _reserva.value = response
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = e.message ?: "Error al obtener reserva",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }
    fun createPayment(reservaId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Paso 1: Crear el pago
                val paymentResponse = paymentApiService.createPayment(PaymentCreateDTO(reservaId))

                if (paymentResponse.payment == null) {
                    throw Exception(paymentResponse.message ?: "Error al crear pago")
                }

                // Paso 2: Crear la venta asociada
                val saleResponse = saleApiService.createSale(
                    SaleCreateDTO(
                        reserva_id = reservaId,
                        payment_id = paymentResponse.payment.id!!
                    )
                )

                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Pago y venta registrados correctamente",
                        notification = NotificationState(
                            message = "Transacción completada. Código: ${paymentResponse.payment.code}",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = "Error en transacción: ${e.message}",
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

