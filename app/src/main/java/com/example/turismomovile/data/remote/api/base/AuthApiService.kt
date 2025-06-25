package com.example.turismomovile.data.remote.api.base

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.dto.LoginDTO
import com.example.turismomovile.data.remote.dto.LoginInput
import com.example.turismomovile.data.remote.dto.LoginResponse
import com.example.turismomovile.data.remote.dto.RegisterResponse
import com.example.turismomovile.data.remote.dto.decodeToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(
    client: HttpClient,
    override val sessionManager: SessionManager
) : BaseApiService(client, sessionManager) {

    suspend fun login(loginDTO: LoginDTO): LoginResponse {
        println("Iniciando el proceso de login...")
        val response = client.post(ApiConstants.Configuration.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginDTO)
        }

        println("Respuesta obtenida del servidor: ${response.status}")

        val loginResponse = response.body<LoginResponse>()
        println("Datos de loginResponse: $loginResponse")

        val token = loginResponse.data.token
        println("Token obtenido: $token")

        val decoded = decodeToken(token)
        println("Token decodificado: $decoded")

        loginResponse.data.username.email?.let {
            println("Creando objeto User con datos del login...")

            val user = User(
                id = loginResponse.data.username.id.toString(),
                email = it,
                name = loginResponse.data.username.username,
                last_name = decoded?.last_name ?: "",
                fullName = decoded?.fullName,
                username = decoded?.username ?: loginResponse.data.username.username,
                code = decoded?.code,
                imagenUrl = decoded?.imagenUrl,
                roles = decoded?.roles ?: emptyList(),
                permissions = decoded?.permissions ?: emptyList(),
                created_at = decoded?.created_at,
                token = token
            )

            println("Usuario creado: $user")
            sessionManager.saveUser(user)
            println("Usuario guardado en la sesión.")
        }

        updateAuthToken(token)
        println("Token de autenticación actualizado.")

        return loginResponse
    }

    suspend fun register(registerInput: LoginInput): RegisterResponse {
        println("Iniciando el proceso de registro...")
        val response = client.post(ApiConstants.Configuration.REGISTER_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(registerInput)
        }

        println("Respuesta obtenida del servidor: ${response.status}")

        val registerResponse = response.body<RegisterResponse>()
        println("Datos de registerResponse: $registerResponse")

        registerResponse.data?.let { data ->
            println("Token obtenido del registro: ${data.token}")
            val token = data.token
            val decoded = decodeToken(token)
            println("Token decodificado: $decoded")

            val user = User(
                id = data.user.id.toString(),
                email = data.user.email,
                name = data.user.username,
                last_name = decoded?.last_name ?: "",
                fullName = decoded?.fullName,
                username = decoded?.username ?: data.user.username,
                code = decoded?.code,
                imagenUrl = decoded?.imagenUrl,
                roles = decoded?.roles ?: emptyList(),
                permissions = decoded?.permissions ?: emptyList(),
                created_at = decoded?.created_at,
                token = token
            )

            println("Usuario creado: $user")
            sessionManager.saveUser(user)
            println("Usuario guardado en la sesión.")
            updateAuthToken(token)
            println("Token de autenticación actualizado.")
        }

        return registerResponse
    }

    suspend fun logout() {
        println("Iniciando el proceso de logout...")
        try {
            val token = sessionManager.getAuthToken()
            println("Token de autenticación obtenido: $token")

            val response = client.post(ApiConstants.Configuration.LOGOUT_ENDPOINT) {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            println("Respuesta de logout: ${response.status}")

            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Unauthorized) {
                sessionManager.clearSession()
                println("Sesión cerrada correctamente.")
            } else {
                println("Error al cerrar sesión: ${response.status.description}")
            }
        } catch (e: Exception) {
            println("Excepción al intentar cerrar sesión: ${e.localizedMessage}")
        }
    }

    override suspend fun loadAuthTokenFromStorage() {
        println("Cargando token de autenticación desde el almacenamiento...")
        sessionManager.getAuthToken()?.let { token ->
            println("Token cargado: $token")
            updateAuthToken(token)
            println("Token de autenticación actualizado.")
        }
    }
}
