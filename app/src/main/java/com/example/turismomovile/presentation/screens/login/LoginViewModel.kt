package com.example.turismomovile.presentation.screens.login

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

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            println("Login attempt: Username = $username, Password = $password")

            _loginState.value = LoginState.Loading

            try {
                val result = loginUseCase(username, password)

                if (result.isSuccess) {
                    val user = result.getOrNull()!!

                    try {
                        sessionManager.saveUser(user)
                    } catch (e: Exception) {
                        println("Error saving user session: ${e.message}")
                        _loginState.value = LoginState.Error("No se pudo guardar la sesión. Intenta nuevamente.")
                        return@launch
                    }

                    println("Login success: User ID = ${user.id}, Name = ${user.name}, Email = ${user.email}")
                    _loginState.value = LoginState.Success(user)

                } else {
                    val errorMessage = "Correo o contraseña incorrectos. Inténtalo nuevamente."
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: IOException) {
                println("IOException: ${e.message}")
                _loginState.value = LoginState.Error("Problema de conexión. Verifica tu red.")
            } catch (e: TimeoutException) {
                println("TimeoutException: ${e.message}")
                _loginState.value = LoginState.Error("La solicitud ha tardado demasiado. Intenta más tarde.")
            } catch (e: Exception) {
                println("Exception during login: ${e.message}")
                _loginState.value = LoginState.Error("Error inesperado. Por favor, intenta más tarde.")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Initial
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}