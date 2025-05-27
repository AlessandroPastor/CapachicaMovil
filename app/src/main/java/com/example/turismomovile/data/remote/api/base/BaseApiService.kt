package com.example.turismomovile.data.remote.api.base

import com.example.turismomovile.data.remote.dto.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*

abstract class BaseApiService(
    protected val client: HttpClient
) {
    private var _authToken: String? = null
    val authToken: String? get() = _authToken

    // Función para agregar el token de autenticación a las peticiones HTTP
    protected fun HttpRequestBuilder.addAuthHeader() {
        if (_authToken.isNullOrEmpty()) {
            throw IllegalStateException("No auth token available. Did you forget to call loadAuthTokenFromStorage()?")
        }
        header(HttpHeaders.Authorization, "Bearer $_authToken")
    }

    // Guardar el token directamente
    fun setAuthToken(token: String?) {
        _authToken = token
    }

    // Si aún deseas compatibilidad con LoginResponse completo
    fun saveLoginResponse(loginResponse: LoginResponse) {
        _authToken = loginResponse.data.token
    }
}
