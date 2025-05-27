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

    override suspend fun loadAuthTokenFromStorage() {
        sessionManager.getUser()?.token?.let {
            updateAuthToken(it)
        }
    }


}




