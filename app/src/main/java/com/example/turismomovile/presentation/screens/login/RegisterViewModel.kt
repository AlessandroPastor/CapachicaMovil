package com.example.turismomovile.presentation.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.domain.usecase.RegisterUseCase
import com.example.turismomovile.data.remote.dto.LoginInput
import com.example.turismomovile.data.remote.dto.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeoutException

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState = _registerState.asStateFlow()

    fun register(registerInput: LoginInput) {
        viewModelScope.launch {
            // Cambiar el estado a Loading mientras el proceso de registro está en curso
            _registerState.value = RegisterState.Loading

            try {
                // Llamamos al UseCase para registrar al usuario
                val result = registerUseCase(registerInput)

                // Verificar si el resultado fue exitoso
                if (result.isSuccess) {
                    val response = result.getOrNull()!!

                    response.data?.let { data ->
                        // Si el registro es exitoso, guardar los datos del usuario y el token
                        Log.d("RegisterViewModel", "Registro exitoso: ${data.user.username}")
                        sessionManager.saveUser(
                            User(
                                id = data.user.id.toString(),
                                name = data.user.username,
                                email = data.user.email,
                                token = data.token
                            )
                        )
                        // Sincronizar el token con el SessionManager
                        sessionManager.saveAuthToken(data.token)
                    }

                    // Actualizar el estado a Success
                    _registerState.value = RegisterState.Success(response)
                } else {
                    // Si no fue exitoso, mostrar el error
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al registrar el usuario. Inténtalo de nuevo."
                    Log.e("RegisterViewModel", "Error en registro: $errorMsg")
                    _registerState.value = RegisterState.Error(errorMsg)
                }

            } catch (e: IOException) {
                // Error de red
                Log.e("RegisterViewModel", "IOException: ${e.message}")
                _registerState.value = RegisterState.Error("Problema de conexión. Verifica tu red.")
            } catch (e: TimeoutException) {
                // Error de tiempo de espera
                Log.e("RegisterViewModel", "TimeoutException: ${e.message}")
                _registerState.value = RegisterState.Error("La solicitud ha tardado demasiado. Intenta más tarde.")
            } catch (e: Exception) {
                // Manejo de excepción en caso de fallo inesperado
                Log.e("RegisterViewModel", "Exception en el proceso de registro: ${e.message}")
                _registerState.value = RegisterState.Error("Error inesperado. Por favor, intenta más tarde.")
            }
        }
    }
    fun resetState() {
        _registerState.value = RegisterState.Initial
    }

    // Estados posibles del registro
    sealed class RegisterState {
        object Initial : RegisterState()
        object Loading : RegisterState()
        data class Success(val response: RegisterResponse) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}
