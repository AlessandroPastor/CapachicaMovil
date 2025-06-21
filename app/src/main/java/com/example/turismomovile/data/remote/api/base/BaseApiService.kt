package com.example.turismomovile.data.remote.api.base

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.dto.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*

open class BaseApiService(
    protected val client: HttpClient,
    protected open val sessionManager: SessionManager
) {
    var authToken: String? = null

    // No suspend, solo añade el header con token ya cargado
    protected suspend fun HttpRequestBuilder.addAuthHeader() {
        if (authToken == null) {
            loadAuthTokenFromStorage()
        }
        authToken?.let {
            header("Authorization", "Bearer $it")
        } ?: throw IllegalStateException("No auth token available. Please login first.")
    }

    fun updateAuthToken(token: String) {  // Cambia el nombre
        this.authToken = token
    }
    fun clearAuthToken() {
        this.authToken = null
    }


    open suspend fun loadAuthTokenFromStorage() {
        authToken = sessionManager.getAuthToken() ?: sessionManager.getUser()?.token
    }
}
