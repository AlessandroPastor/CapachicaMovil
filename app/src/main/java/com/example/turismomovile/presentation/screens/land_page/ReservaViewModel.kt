package com.example.turismomovile.presentation.screens.land_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.remote.api.ventas.PaymentApiService
import com.example.turismomovile.data.remote.api.ventas.ReservaApiService
import com.example.turismomovile.data.remote.dto.configuracion.Producto
import com.example.turismomovile.data.remote.dto.ventas.*
import com.example.turismomovile.presentation.components.NotificationState
import com.example.turismomovile.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---- Modelo de CartItem para el carrito ---- //
data class CartItem(
    val producto: Producto,
    val cantidadSeleccionada: Int,
    val lugar: String? = null // opcional
)

class ReservaViewModel(
    private val reservaApiService: ReservaApiService,
    private val paymentApiService: PaymentApiService

) : ViewModel() {

    private val _state = MutableStateFlow(ReservaState())
    val state = _state.asStateFlow()

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito = _carrito.asStateFlow()

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
                _state.update {
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


    // ---- NUEVO: Métodos para manejar el carrito ---- //
    fun agregarAlCarrito(producto: Producto, cantidad: Int, lugar: String? = null) {
        val actualizado = _carrito.value.toMutableList()
        val idx = actualizado.indexOfFirst { it.producto.emprendedor_service_id == producto.emprendedor_service_id }
        if (idx >= 0) {
            // Si ya está, suma la cantidad
            val existente = actualizado[idx]
            actualizado[idx] = existente.copy(cantidadSeleccionada = existente.cantidadSeleccionada + cantidad)
        } else {
            actualizado.add(CartItem(producto, cantidad, lugar))
        }
        _carrito.value = actualizado
    }

    fun actualizarCantidad(item: CartItem, nuevaCantidad: Int) {
        _carrito.value = _carrito.value.map {
            if (it.producto.emprendedor_service_id == item.producto.emprendedor_service_id) it.copy(cantidadSeleccionada = nuevaCantidad)
            else it
        }
    }

    fun quitarDelCarrito(item: CartItem) {
        _carrito.value = _carrito.value.filterNot { it.producto.emprendedor_service_id == item.producto.emprendedor_service_id }
    }

    fun limpiarCarrito() {
        _carrito.value = emptyList()
    }

    // ---- CONVERTIR CARRITO A DTO Y ENVIAR RESERVA ---- //
    fun reservarAhora() {
        val detalles = _carrito.value.mapNotNull { item ->
            item.producto.emprendedor_service_id?.let { id ->
                ReservaDetalleCreateDTO(
                    emprendedor_service_id = id,
                    cantidad = item.cantidadSeleccionada,
                    lugar = item.lugar
                )
            }
        }
        if (detalles.isEmpty()) {
            _state.update {
                it.copy(
                    notification = NotificationState(
                        message = "El carrito está vacío",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
            return
        }
        val dto = ReservaCreateDTO(details = detalles)
        createReserva(dto)
    }

    fun createReserva(dto: ReservaCreateDTO) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val reservaResponse = reservaApiService.createReserva(dto)
                val reservaDetail = reservaApiService.getReservaById(reservaResponse.reserva.toString())
                val paymentResponse = paymentApiService.createPayment(
                    PaymentCreateDTO(reservaResponse.reserva.toString())
                )
                limpiarCarrito() // Limpia el carrito al reservar
                loadReservas()
                _state.update {
                    it.copy(
                        notification = NotificationState(
                            message = "Reserva creada con código ${reservaDetail.code}. Continúa con el proceso de pago.",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        ),
                        isLoading = false,
                        isDialogOpen = true,
                        selectedItem = null,
                        lastCreatedReservaId = reservaResponse.reserva.toString(),
                        lastCreatedReservaCode = reservaDetail.code
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = e.message ?: "Error al crear reserva",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }

    fun deleteReserva(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                reservaApiService.deleteReserva(id)
                loadReservas()
                _state.update {
                    it.copy(
                        notification = NotificationState(
                            message = "Reserva eliminada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        ),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = e.message ?: "Error al eliminar reserva",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }
    fun clearNavigationState() {
        _state.update { it.copy(lastCreatedReservaId = null) }
    }


    fun dismissSuccessDialog() {
        _state.update { it.copy(isDialogOpen = false, lastCreatedReservaCode = null) }
    }
}