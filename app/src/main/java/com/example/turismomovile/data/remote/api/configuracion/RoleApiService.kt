package com.example.turismomovile.data.remote.api.configuracion

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.configuracion.Role
import com.example.turismomovile.data.remote.dto.configuracion.RoleModulesRequest
import com.example.turismomovile.data.remote.dto.configuracion.RoleResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*


class RoleApiService(client: HttpClient, sessionManager: SessionManager) : BaseApiService(client,
    sessionManager
) {

    suspend fun getRoles(page: Int = 0, size: Int = 20, name: String? = null): RoleResponse {
        return client.get(ApiConstants.Configuration.ROLES) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            addAuthHeader()
        }.body()
    }

    suspend fun getRoleById(id: String): Role {
        return client.get(ApiConstants.Configuration.ROLE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    suspend fun createRole(role: Role): Role {
        return client.post(ApiConstants.Configuration.ROLES) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(role)
        }.body()
    }

    suspend fun updateRole(id: String, role: Role): Role {
        return client.put(ApiConstants.Configuration.ROLE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(role)
        }.body()
    }

    suspend fun deleteRole(id: String) {
        client.delete(ApiConstants.Configuration.ROLE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }
    }

    suspend fun updateRoleModules(request: RoleModulesRequest): Boolean {
        val response: HttpResponse = client.post(ApiConstants.Configuration.ROLE_BY_MODULE) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status == HttpStatusCode.OK
    }
}
