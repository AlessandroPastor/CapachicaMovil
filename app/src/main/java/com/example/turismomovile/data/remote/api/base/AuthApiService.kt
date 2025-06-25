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
        val response = client.post(ApiConstants.Configuration.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginDTO)
        }

        val loginResponse = response.body<LoginResponse>()

        val token = loginResponse.data.token
        val decoded = decodeToken(token)

        loginResponse.data.username.email?.let {
            User(
                id = loginResponse.data.username.id.toString(),
                email = it,
                name = loginResponse.data.username.username,
                lastName = decoded?.lastName ?: "",
                fullName = decoded?.fullName,
                username = decoded?.username ?: loginResponse.data.username.username,
                code = decoded?.code,
                imagenUrl = decoded?.imagenUrl,
                roles = decoded?.roles ?: emptyList(),
                permissions = decoded?.permissions ?: emptyList(),
                createdAt = decoded?.createdAt,
                token = token
            )
        }?.let {
            sessionManager.saveUser(
                it
            )
        }

        updateAuthToken(token)

        return loginResponse
    }

    suspend fun register(registerInput: LoginInput): RegisterResponse {
        val response = client.post(ApiConstants.Configuration.REGISTER_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(registerInput)
        }

        val registerResponse = response.body<RegisterResponse>()

        registerResponse.data?.let { data ->
            val token = data.token
            val decoded = decodeToken(token)

            sessionManager.saveUser(
                User(
                    id = data.user.id.toString(),
                    email = data.user.email,
                    name = data.user.username,
                    lastName = decoded?.lastName ?: "",
                    fullName = decoded?.fullName,
                    username = decoded?.username ?: data.user.username,
                    code = decoded?.code,
                    imagenUrl = decoded?.imagenUrl,
                    roles = decoded?.roles ?: emptyList(),
                    permissions = decoded?.permissions ?: emptyList(),
                    createdAt = decoded?.createdAt,
                    token = token
                )
            )

            updateAuthToken(token)
        }

        return registerResponse
    }

    suspend fun logout() {
        try {
            val token = sessionManager.getAuthToken()
            val response = client.post(ApiConstants.Configuration.LOGOUT_ENDPOINT) {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Unauthorized) {
                sessionManager.clearSession()
                println("Sesión cerrada correctamente")
            } else {
                println("Error al cerrar sesión: ${response.status.description}")
            }
        } catch (e: Exception) {
            println("Excepción al intentar cerrar sesión: ${e.localizedMessage}")
        }
    }

    override suspend fun loadAuthTokenFromStorage() {
        sessionManager.getAuthToken()?.let { token ->
            updateAuthToken(token)
        }
    }
}
