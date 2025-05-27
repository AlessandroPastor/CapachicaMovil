package com.example.turismomovile.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.dto.MenuItem
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val currentScreenTitle: String = "Home",
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedMenuItems: Set<String> = emptySet(),
    val isDrawerOpen: Boolean = false
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initializeData()
    }

    fun initializeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Recuperar usuario guardado
                val savedUser = sessionManager.getUser()
                if (savedUser != null) {

                    // Establecer el usuario inmediatamente
                    _uiState.value = _uiState.value.copy(user = savedUser)

                    // Cargar el menú inmediatamente al encontrar usuario guardado
                    authRepository.getMenuItems()
                        .onSuccess { menuItems ->
                            _uiState.value = _uiState.value.copy(
                                menuItems = menuItems,
                                isLoading = false
                            )
                        }
                        .onFailure { error -> // Si falla la carga del menú, intentamos refrescar el token
                            refreshTokenAndLoadMenu()
                        }

                    // Intentar refrescar los datos del usuario en segundo plano
                    authRepository.getUserDetails()
                        .onSuccess { updatedUser ->
                            _uiState.value = _uiState.value.copy(user = updatedUser)
                            sessionManager.saveUser(updatedUser)
                        }
                        .onFailure { error ->
                        }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de inicialización: ${e.message}"
                )
            }
        }
    }


    private suspend fun loadMenuItems() {
        try {
            authRepository.getMenuItems()
                .onSuccess { menuItems ->
                    _uiState.value = _uiState.value.copy(
                        menuItems = menuItems,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Error al cargar menú: ${error.message}",
                        isLoading = false
                    )
                }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Error inesperado al cargar menú: ${e.message}",
                isLoading = false
            )
        }
    }

    private suspend fun retryLoadMenuWithRefresh() {
        // Intentar refrescar el token mediante una nueva llamada a getUserDetails
        authRepository.getUserDetails()
            .onSuccess { user ->
                // Si se actualizó el usuario correctamente, intentar cargar el menú nuevamente
                authRepository.getMenuItems()
                    .onSuccess { menuItems ->
                        _uiState.value = _uiState.value.copy(
                            menuItems = menuItems,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al cargar menú: ${error.message}"
                        )
                    }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al refrescar sesión: ${error.message}"
                )
            }
    }
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession() // 🔹 Borrar la sesión en almacenamiento
            // 🔹 Limpiar completamente el estado UIState y asegurarnos de que no haya usuario
            _uiState.value = HomeUiState(
                user = null, // ✅ Aseguramos que el usuario sea `null`
                menuItems = emptyList(),
                currentScreenTitle = "Home",
                isLoading = false,
                error = null
            )
        }
    }

    fun loadUserAndMenu() {
        viewModelScope.launch {
            setLoading(true) // 🔹 Activamos el loader

            try {
                val savedUser = sessionManager.getUser()
                if (savedUser != null) {
                    _uiState.value = _uiState.value.copy(user = savedUser)
                    loadMenuItems()

                    authRepository.getUserDetails()
                        .onSuccess { user ->
                            _uiState.value = _uiState.value.copy(user = user)
                            sessionManager.saveUser(user)
                        }
                        .onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                error = "Error al actualizar usuario: ${error.message}"
                            )
                        }
                } else {
                    authRepository.getUserDetails()
                        .onSuccess { user ->
                            _uiState.value = _uiState.value.copy(user = user)
                            sessionManager.saveUser(user)
                            loadMenuItems()
                        }
                        .onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                error = "Error al cargar usuario: ${error.message}"
                            )
                        }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error inesperado: ${e.message}"
                )
            } finally {
                setLoading(false) // 🔹 Desactivamos el loader al finalizar
            }
        }
    }



    fun toggleMenuItem(menuId: String) {
        val currentExpanded = _uiState.value.expandedMenuItems
        _uiState.value = if (currentExpanded.contains(menuId)) {
            _uiState.value.copy(expandedMenuItems = currentExpanded - menuId)
        } else {
            _uiState.value.copy(expandedMenuItems = currentExpanded + menuId)
        }
    }

    fun setDrawerOpen(isOpen: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDrawerOpen = isOpen)

            // Si el drawer se abre y no hay menú items, intentar cargar
            if (isOpen && _uiState.value.menuItems.isEmpty()) {
                loadMenuItems()
            }
        }
    }

    fun updateCurrentScreen(title: String) {
        _uiState.value = _uiState.value.copy(currentScreenTitle = title)
    }

    fun getCurrentScreenTitle(): String {
        return _uiState.value.currentScreenTitle
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setCurrentScreenTitle(route: String) {
        val title = _uiState.value.menuItems
            .flatMap { menuItem ->
                listOfNotNull(menuItem) + (menuItem.children ?: emptyList())
            }
            .find { it.link == route }
            ?.title ?: "Home"

        updateCurrentScreen(title)
    }
    private suspend fun refreshTokenAndLoadMenu() {
        authRepository.getUserDetails()
            .onSuccess { user ->
                _uiState.value = _uiState.value.copy(user = user)
                sessionManager.saveUser(user)

                // Intentar cargar el menú nuevamente con el token actualizado
                authRepository.getMenuItems()
                    .onSuccess { menuItems ->
                        _uiState.value = _uiState.value.copy(
                            menuItems = menuItems,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al cargar menú: ${error.message}"
                        )
                    }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al refrescar sesión: ${error.message}"
                )
            }
    }

}