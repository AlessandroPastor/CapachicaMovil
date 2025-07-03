package com.example.turismomovile.data.remote.api.ventas

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.ventas.ReservaCreateDTO
import com.example.turismomovile.data.remote.dto.ventas.ReservaDetalleCreateDTO
import com.example.turismomovile.data.remote.dto.ventas.ReservaDetalleResponse
import com.example.turismomovile.data.remote.dto.ventas.ReservaListResponse
import com.example.turismomovile.data.remote.dto.ventas.ReservaUsuarioDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ReservaApiService(
    client: HttpClient,
    sessionManager: SessionManager
    ) : BaseApiService(client, sessionManager) {

        suspend fun getReservas(page: Int = 0, size: Int = 10,search: String?): ReservaListResponse {
            val response = client.get(ApiConstants.Configuration.RESERVA_ENDPOINT) {
                addAuthHeader()
                parameter("page", page)
                parameter("size", size)
                search?.let { parameter("search", it) }
              }
            return response.body()
        }

       suspend fun getReservaById(id: String): ReservaUsuarioDTO {
           val endpoint = ApiConstants.Configuration.RESERVA_GET_BYID.replace("{id}", id)
           return client.get(endpoint) {
               addAuthHeader()
           }.body()
       }

    suspend fun createReserva(reserva: ReservaCreateDTO): ReservaDetalleResponse {
        println("Iniciando la solicitud POST para crear la reserva...")

        val response = client.post(ApiConstants.Configuration.RESERVA_POST) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(reserva)
        }

        // Imprimir detalles de la respuesta recibida
        println("Respuesta recibida: ${response.status}")

        val bodyResponse = response.body<ReservaDetalleResponse>()
        println("Cuerpo de la respuesta: $bodyResponse")

        return bodyResponse
    }


    suspend fun updateReserva(id: String, reserva: ReservaDetalleCreateDTO): ReservaDetalleResponse {
           val endpoint = ApiConstants.Configuration.RESERVA_PUT.replace("{id}", id)
           val response = client.put(endpoint) {
               addAuthHeader()
               contentType(ContentType.Application.Json)
               setBody(reserva)
           }
           return response.body()
       }

       suspend fun deleteReserva(id: String) {
            val endpoint = ApiConstants.Configuration.RESERVA_DELETE.replace("{id}", id)
             client.delete(endpoint) {
                       addAuthHeader()
             }
       }
    }
