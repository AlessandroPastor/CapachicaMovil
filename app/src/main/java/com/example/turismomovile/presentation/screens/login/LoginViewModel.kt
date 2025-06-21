package com.example.turismomovile.presentation.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeoutException

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Estado de login
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    // Función de login
    fun login(username: String, password: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Login attempt: Username = $username, Password = $password")

            // Establecemos el estado de carga
            _loginState.value = LoginState.Loading

            try {
                // Llamamos al caso de uso para hacer el login
                val result = loginUseCase(username, password)

                if (result.isSuccess) {
                    val user = result.getOrNull()!!

                    // Intentamos guardar el usuario en el SessionManager
                    try {
                        sessionManager.saveUser(user)
                    } catch (e: Exception) {
                        // Si no se puede guardar el usuario, capturamos el error
                        Log.e("LoginViewModel", "Error saving user session: ${e.message}")
                        _loginState.value = LoginState.Error("No se pudo guardar la sesión. Intenta nuevamente.")
                        return@launch
                    }

                    // Si el login es exitoso y el usuario se guarda correctamente
                    Log.d("LoginViewModel", "Login success: User ID = ${user.id}, Name = ${user.name}, Email = ${user.email}")
                    _loginState.value = LoginState.Success(user)

                } else {
                    // Si el resultado de login falla, se maneja el error
                    val errorMessage = "Correo o contraseña incorrectos. Inténtalo nuevamente."
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: IOException) {
                // Error de red
                Log.e("LoginViewModel", "IOException: ${e.message}")
                _loginState.value = LoginState.Error("Problema de conexión. Verifica tu red.")
            } catch (e: TimeoutException) {
                // Error de tiempo de espera
                Log.e("LoginViewModel", "TimeoutException: ${e.message}")
                _loginState.value = LoginState.Error("La solicitud ha tardado demasiado. Intenta más tarde.")
            } catch (e: Exception) {
                // Error general
                Log.e("LoginViewModel", "Exception during login: ${e.message}")
                _loginState.value = LoginState.Error("Error inesperado. Por favor, intenta más tarde.")
            }
        }
    }
}

// Clases de estado para manejar el flujo de la UI
sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
