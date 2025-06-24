package com.example.turismomovile.data.remote.api.base




import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User

import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.dto.LoginDTO
import com.example.turismomovile.data.remote.dto.LoginInput
import com.example.turismomovile.data.remote.dto.LoginResponse
import com.example.turismomovile.data.remote.dto.RegisterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(
    client: HttpClient,
    override val sessionManager: SessionManager
) : BaseApiService(client, sessionManager) {  // <-- Aquí pasa sessionManager al padre

    suspend fun login(loginDTO: LoginDTO): LoginResponse {
        val response = client.post(ApiConstants.Configuration.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginDTO)
        }

        val loginResponse = response.body<LoginResponse>()

        sessionManager.saveUser(
            User(
                id = loginResponse.data.username.id.toString(),
                name = loginResponse.data.username.username,
                email = loginResponse.data.username.email,
                token = loginResponse.data.token
            )
        )

        // Sincroniza todos los lugares
        sessionManager.saveAuthToken(loginResponse.data.token)
        updateAuthToken(loginResponse.data.token)

        return loginResponse
    }

    suspend fun register(registerInput: LoginInput): RegisterResponse {
        val response = client.post(ApiConstants.Configuration.REGISTER_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(registerInput)
        }

        val registerResponse = response.body<RegisterResponse>()

        // Solo guardar datos de sesión si la respuesta contiene información
        registerResponse.data?.let { data ->
            // Verificar si ya existe un token antes de guardarlo
            sessionManager.getAuthToken()?.let {
                if (it != data.token) {
                    sessionManager.saveAuthToken(data.token)
                }
            } ?: sessionManager.saveAuthToken(data.token)

            sessionManager.saveUser(
                User(
                    id = data.user.id.toString(),
                    name = data.user.username,
                    email = data.user.email,
                    token = data.token
                )
            )
        }

        return registerResponse
    }


    suspend fun logout() {
        try {
            // Llamar al backend para invalidar el token
            val token = sessionManager.getAuthToken()  // Esto está dentro de una corrutina
            val response = client.post(ApiConstants.Configuration.LOGOUT_ENDPOINT) {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            // Si la respuesta es exitosa, limpiamos la sesión local
            if (response.status == HttpStatusCode.OK) {
                sessionManager.clearSession()  // Limpiar la sesión local
                println("Sesión cerrada exitosamente")
            } else {
                println("Error al cerrar sesión: ${response.status.description}")
            }
        } catch (e: Exception) {
            // Manejar cualquier excepción de la llamada a la API
            println("Error al intentar cerrar sesión: ${e.message}")
        }
    }





    override suspend fun loadAuthTokenFromStorage() {
        // Cargar el token desde el SessionManager
        sessionManager.getUser()?.token?.let {
            updateAuthToken(it)
        }
    }

}




