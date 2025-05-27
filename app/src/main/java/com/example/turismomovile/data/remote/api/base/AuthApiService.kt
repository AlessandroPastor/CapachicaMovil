package com.example.turismomovile.data.remote.api.base




import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User

import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.dto.LoginDTO
import com.example.turismomovile.data.remote.dto.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(
    client: HttpClient,
    private val sessionManager: SessionManager
) : BaseApiService(client) {

    suspend fun login(loginDTO: LoginDTO): LoginResponse {
        return try {
            val response = client.post(ApiConstants.Configuration.LOGIN_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(loginDTO)
            }

            val loginResponse = response.body<LoginResponse>()

            // Guardamos el token del usuario
            sessionManager.saveUser(
                User(
                    id = loginResponse.data.username.id.toString(),
                    name = loginResponse.data.username.username,
                    email = loginResponse.data.username.email,
                    token = loginResponse.data.token
                )
            )

            // También lo asignamos internamente
            setAuthToken(loginResponse.data.token)

            loginResponse
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun loadAuthTokenFromStorage() {
        sessionManager.getUser()?.token?.let {
            setAuthToken(it)
        }
    }
}



