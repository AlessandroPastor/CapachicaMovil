package com.example.turismomovile.data.remote.api.configuracion

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.configuracion.AsignarServiciosRequest
import com.example.turismomovile.data.remote.dto.configuracion.AsignarServiciosResponse
import com.example.turismomovile.data.remote.dto.configuracion.Emprendedor
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorByUserResponse
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorResponse
import com.example.turismomovile.data.remote.dto.configuracion.ReporteVentasResponse
import com.example.turismomovile.data.remote.dto.configuracion.ReservasPorEmprendedorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EmprendedorApiService(
    client: HttpClient,
    sessionManager: SessionManager
) : BaseApiService(client, sessionManager) {

    // Obtener lista paginada de emprendedores con filtro por name
    suspend fun getEmprendedor(page: Int = 0, size: Int = 10, name: String? = null): EmprendedorResponse {
        return client.get(ApiConstants.Configuration.EMPRENDEDORES_GET) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
        }.body()
    }

    // Obtener un emprendedor por ID
    suspend fun getEmprendedorById(id: String): Emprendedor {
        return client.get(ApiConstants.Configuration.EMPRENDEDORES_GETBYID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    // Crear un nuevo emprendedor
    suspend fun createEmprendedor(request: EmprendedorCreateDTO): Emprendedor {
        return client.post(ApiConstants.Configuration.EMPRENDEDORES_POST) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Actualizar un emprendedor
    suspend fun updateEmprendedor(id: String, request: EmprendedorCreateDTO): Emprendedor {
        return client.put(ApiConstants.Configuration.EMPRENDEDORES_PUT.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Eliminar un emprendedor
    suspend fun deleteEmprendedor(id: String) {
        client.delete(ApiConstants.Configuration.EMPRENDEDORES_DELETE.replace("{id}", id)) {
            addAuthHeader()
        }
    }

    suspend fun asignarServicios(id: String, request: AsignarServiciosRequest): AsignarServiciosResponse {
        return client.post(ApiConstants.Configuration.EMPRENDEDORES_ASIGNAR.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getEmprendedorByUserId(userId: String): EmprendedorByUserResponse {
        return client.get(ApiConstants.Configuration.EMPRENDEDORES_GETBYUSERID.replace("{userId}", userId)) {
            addAuthHeader()
        }.body()
    }

    // Reporte de ventas
    suspend fun reporteVentas(emprendedorId: String, page: Int = 0, size: Int = 10): ReporteVentasResponse {
        return client.get(ApiConstants.Configuration.REPORTE_VENTAS.replace("{id}", emprendedorId)) {
            parameter("page", page)
            parameter("size", size)
            addAuthHeader()
        }.body()
    }

    // Reservas por emprendedor
    suspend fun reservasPorEmprendedor(emprendedorId: String): ReservasPorEmprendedorResponse {
        return client.get(ApiConstants.Configuration.RESERVAS_POR_EMPRENDEDOR.replace("{id}", emprendedorId)) {
            addAuthHeader()
        }.body()
    }


}
