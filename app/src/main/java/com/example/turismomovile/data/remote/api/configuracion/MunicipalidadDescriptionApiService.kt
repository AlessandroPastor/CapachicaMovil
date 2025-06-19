package com.example.turismomovile.data.remote.api.configuracion

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.configuracion.ApiResponseWrapper
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescription
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionResponse
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionUpdateDto
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MunicipalidadDescriptionApiService (client: HttpClient,
                                          sessionManager: SessionManager
) : BaseApiService(client, sessionManager){

    suspend fun getMunicipalidadDesc(page: Int = 0, size: Int = 20, name: String? = null): MunicipalidadDescriptionResponse {
        val response = client.get(ApiConstants.Configuration.MUNICIPALIDAD_DESCRIPTION) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
        }.body<MunicipalidadDescriptionResponse>()
        return response
    }

    suspend fun getMunicipalidadDescrptionById(id: String): MunicipalidadDescription {
        return client.get(ApiConstants.Configuration.MUNICIPALIDAD_DESCRIPTIONBYID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    /*suspend fun createMunicipalidad(municipalidad: MunicipalidadCreateDTO): Municipalidad
    {
        return client.post(ApiConstants.Configuration.MUNICIPALIDAD_POST) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(municipalidad)
        }.body()
    }*/

    // 2. Modificar tu ApiService
    suspend fun updateMunicipalidadDescription(id: String, municipalidad_id: MunicipalidadDescriptionUpdateDto): MunicipalidadDescription {
        return client.put(ApiConstants.Configuration.MUNICIPALIDAD_DESCRIPTIONBYID_PUT.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(municipalidad_id)
        }.body<ApiResponseWrapper<MunicipalidadDescription>>().data // ✅ Extraer solo el 'data'
    }

    suspend fun deleteMunicipalidad(id: String) {
        client.delete(ApiConstants.Configuration.MUNICIPALIDAD_DESCRIPTIONBYID_DELETE.replace("{id}", id)) {
            addAuthHeader()
        }
    }

}