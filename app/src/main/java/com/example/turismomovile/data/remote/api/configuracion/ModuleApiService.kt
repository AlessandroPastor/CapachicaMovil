package com.example.turismomovile.data.remote.api.configuracion

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.configuracion.ModuleCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ModuleDTO
import com.example.turismomovile.data.remote.dto.configuracion.ModuleResponse
import com.example.turismomovile.data.remote.dto.configuracion.ModuleSelectedDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*


class ModuleApiService(client: HttpClient, sessionManager: SessionManager) : BaseApiService(client,
    sessionManager
) {

    suspend fun getModules(page: Int = 0, size: Int = 5, name: String?): ModuleResponse {
        return client.get(ApiConstants.Configuration.MODULES) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            addAuthHeader()
        }.body()
    }

    suspend fun getModuleById(id: String): ModuleDTO {
        return client.get(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    suspend fun createModule(module: ModuleCreateDTO): ModuleDTO {
        return client.post(ApiConstants.Configuration.MODULES) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(module)
        }.body()
    }

    suspend fun updateModule(id: String, module: ModuleCreateDTO): ModuleDTO {
        return client.put(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(module)
        }.body()
    }

    suspend fun deleteModule(id: String) {
        client.delete(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            addAuthHeader()
        }
    }

    suspend fun getModulesSelected(roleId: String, parentModuleId: String): List<ModuleSelectedDTO> {
        val url = ApiConstants.Configuration.MODULE_SELECTED
            .replace("{roleId}", roleId)
            .replace("{parentModuleId}", parentModuleId)
        return client.get(url) {
            addAuthHeader()
        }.body()
    }
}
