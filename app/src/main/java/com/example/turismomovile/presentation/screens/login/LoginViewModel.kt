package com.example.turismomovile.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            // Depuración: Inicia el proceso de login
            println("Login attempt: Username = $username, Password = $password")

            _loginState.value = LoginState.Loading

            // Depuración: Llamada al UseCase
            try {
                println("Attempting to authenticate user...")
                loginUseCase(username, password)
                    .onSuccess { user ->
                        // Depuración: Exito en login
                        println("Login success: User ID = ${user.id}, Name = ${user.name}, Email = ${user.email}")

                        // Guardar usuario en el SessionManager
                        sessionManager.saveUser(user)

                        // Depuración: Información guardada
                        println("User data saved in sessionManager. User ID: ${user.id}")

                        // Actualizar el estado a Success
                        _loginState.value = LoginState.Success(user)

                    }
                    .onFailure { error ->
                        // Depuración: Error al intentar el login
                        println("Login failed: ${error.message}")

                        // Manejo de errores con mensajes específicos
                        val errorMessage = when {
                            error.message?.contains("Correo o contraseña incorrectos") == true ->
                                "Correo o contraseña incorrectos. Inténtalo nuevamente."

                            error.message?.contains("Error inesperado") == true ->
                                "Ocurrió un problema con el servidor. Inténtalo más tarde."

                            else -> "No se pudo iniciar sesión. Verifica tu conexión e intenta de nuevo."
                        }

                        // Depuración: Error procesado
                        println("Error message processed: $errorMessage")

                        _loginState.value = LoginState.Error(errorMessage)
                    }
            } catch (e: Exception) {
                // Depuración: Excepción no controlada
                println("Exception during login process: ${e.message}")

                _loginState.value =
                    LoginState.Error("Error inesperado. Por favor, intenta más tarde.")
            }
        }
    }
}



sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
