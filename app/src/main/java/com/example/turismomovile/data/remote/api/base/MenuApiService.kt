package com.example.turismomovile.data.remote.api.base


import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.dto.MenuItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MenuApiService(client: HttpClient) : BaseApiService(client) {

    suspend fun getMenuItems(): List<MenuItem> {
        println("📡 Llamando a ${ApiConstants.Configuration.MENU_ENDPOINT}")
        println("🔐 Token enviado: ${authToken}")

        return try {
            val response = client.get(ApiConstants.Configuration.MENU_ENDPOINT) {
                addAuthHeader()
            }

            // Validar código de estado si deseas
            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                println("❌ Error HTTP ${response.status.value}: $errorBody")
                throw Exception("Error de servidor: ${response.status}")
            }

            response.body()
        } catch (e: io.ktor.client.plugins.ResponseException) {
            val errorText = e.response.bodyAsText()
            println("⚠️ ResponseException atrapada: $errorText")
            try {
                val parsed = Json.parseToJsonElement(errorText).jsonObject
                val message = parsed["message"]?.jsonPrimitive?.content
                println("🚨 Mensaje del backend: $message")
            } catch (jsonEx: Exception) {
                println("❗ No se pudo parsear el mensaje de error JSON: ${jsonEx.message}")
            }
            emptyList()
        } catch (e: Exception) {
            println("❌ Error inesperado al obtener menús: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}
