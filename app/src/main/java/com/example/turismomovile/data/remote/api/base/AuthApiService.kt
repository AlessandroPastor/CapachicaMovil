package com.example.turismomovile.data.remote.api.base

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.dto.LoginDTO
import com.example.turismomovile.data.remote.dto.LoginInput
import com.example.turismomovile.data.remote.dto.LoginResponse
import com.example.turismomovile.data.remote.dto.RegisterResponse
import com.example.turismomovile.data.remote.dto.UpdateProfileDTO
import com.example.turismomovile.data.remote.dto.decodeToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
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
            sessionManager.saveUser(user)
            updateAuthToken(token)// token en memoria para futuras llamadas
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
            sessionManager.saveUser(user)
            updateAuthToken(token)
        }
        return registerResponse
    }

    suspend fun updateProfile(profile: UpdateProfileDTO): Boolean {
        val response = client.put(ApiConstants.Configuration.UPDATE_PROFILE_ENDPOINT) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(profile)
        }
        return response.status.value in 200..299
    }
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String? {
        val token = sessionManager.getAuthToken() ?: return null
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = ApiConstants.Configuration.UPLOAD_PHOTO_ENDPOINT,
            formData = formData {
                append("photo", imageBytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=$fileName")
                    append(HttpHeaders.ContentType, "image/jpeg")
                })
            }
        ) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        val json = response.bodyAsText()
        val match = "\"url\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(json)
        // El replace aquí, para limpiar cualquier barra invertida rara
        return match?.groups?.get(1)?.value?.replace("\\", "")
    }

    override suspend fun loadAuthTokenFromStorage() {
        sessionManager.getAuthToken()?.let { token ->
            updateAuthToken(token)
        }
    }
}
