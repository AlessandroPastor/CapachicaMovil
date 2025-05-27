package com.example.turismomovile.data.remote.api.configuracion

import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociaciones
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesByAsoacionesResponse
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*


class ImgAsociacionesApiService (client: HttpClient) : BaseApiService(client){


    suspend fun getImgAsoaciones(): ImgAsociacionesResponse {
        println("🔄 Iniciando solicitud para obtener imágenes de asociaciones...")

        // Agrega detalles de la solicitud
        println("   🌐 URL: ${ApiConstants.Configuration.IMG_ASOCIACIONES_GET}")

        val response = try {
            // Realizar la solicitud HTTP GET
            client.get(ApiConstants.Configuration.IMG_ASOCIACIONES_GET) {
                // Puedes agregar parámetros si es necesario
            }.body<ImgAsociacionesResponse>().also {
                // Imprimir la respuesta cruda para depuración
                println("   📥 Respuesta recibida:")
                println("   ${it}")
            }
        } catch (e: Exception) {
            // Si ocurre un error, imprimir el mensaje de error
            println("❌ Error al intentar obtener las imágenes de asociaciones: ${e.message}")
            throw e
        }

        // Imprimir la respuesta final para verificar los datos
        println("✔️ Respuesta de imágenes de asociaciones recibida correctamente")
        return response
    }


    suspend fun getImgAsoacionesByAsoaciones(asociacionId: String): ImgAsociacionesByAsoacionesResponse {
        println("🔄 Iniciando solicitud para obtener imágenes de asociaciones por asociación...")

        // Agregar detalles de la solicitud con el 'asociacionId' en la URL
        val url = "${ApiConstants.Configuration.IMG_ASOCIACIONES_GET_ASOCIACIONES}/$asociacionId"
        println("   🌐 URL: $url")

        val response = try {
            // Realizar la solicitud HTTP GET con el 'asociacionId' en la URL
            client.get(url) {
                // Aquí puedes agregar otros parámetros si es necesario
            }.body<ImgAsociacionesByAsoacionesResponse>().also {
                // Imprimir la respuesta cruda para depuración
                println("   📥 Respuesta recibida:")
                println("   ${it}")
            }
        } catch (e: Exception) {
            // Si ocurre un error, imprimir el mensaje de error
            println("❌ Error al intentar obtener las imágenes de asociaciones por asociación: ${e.message}")
            throw e
        }

        // Imprimir la respuesta final para verificar los datos
        println("✔️ Respuesta de imágenes de asociaciones por asociación recibida correctamente")
        return response
    }




    suspend fun getImgAsoacionesById(id: String): ImgAsociaciones {
        return client.get(ApiConstants.Configuration.IMG_ASOCIACIONES_GET_BYID.replace("{id}", id)) {
            addAuthHeader()
        }.body()
    }

    suspend fun createImgAsoaciones(imgAsociaciones: ImgAsociacionesCreateDTO): ImgAsociaciones

    {
        return client.post(ApiConstants.Configuration.IMG_ASOCIACIONES_POST) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(imgAsociaciones)
        }.body()
    }

    suspend fun updateImgAsoaciones(id: String, imgasoaciones: ImgAsociaciones): ImgAsociaciones {
        return client.put(ApiConstants.Configuration.IMG_ASOCIACIONES_PUT.replace("{id}", id)) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(imgasoaciones)
        }.body()
    }

    suspend fun deleteImgAsocioanes(id: String) {
        client.delete(ApiConstants.Configuration.IMG_ASOCIACIONES_DELETE.replace("{id}", id)) {
            addAuthHeader()
        }
    }

}