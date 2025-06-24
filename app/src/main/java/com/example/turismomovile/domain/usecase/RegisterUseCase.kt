package com.example.turismomovile.domain.usecase

import com.example.turismomovile.data.remote.dto.LoginInput
import com.example.turismomovile.data.remote.dto.RegisterResponse
import com.example.turismomovile.data.remote.api.base.AuthApiService
import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.toUser

class RegisterUseCase(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager  // Asegúrate de tener acceso al SessionManager
) {

    suspend operator fun invoke(registerInput: LoginInput): Result<RegisterResponse> {
        return try {
            // Limpiar la sesión antes de registrar un nuevo usuario
            sessionManager.clearSession()  // Limpiar cualquier sesión previa

            // Realiza el registro llamando al servicio
            val response = authApiService.register(registerInput)
            if (response.status && response.data != null) {
                // Acceder a los datos de la respuesta: `data.user` y `data.token`
                val userResponse = response.data.user
                val token = response.data.token

                // Convertir UserResponse a User
                val user = userResponse.toUser(token)

                // Guardar el token en la sesión
                sessionManager.saveAuthToken(token)

                // Guardar el usuario en la sesión
                sessionManager.saveUser(user)
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            // Captura cualquier excepción y devuelve un resultado con el error
            Result.failure(e)
        }
    }
}
