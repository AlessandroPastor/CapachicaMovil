package com.example.turismomovile.data.remote.dto

import com.example.turismomovile.domain.model.User
import io.ktor.util.decodeBase64String
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val status: Boolean,
    val message: String,
    val data: LoginData
)

@Serializable
data class LoginData(
    val token: String,
    val expires_at: String,
    val username: Username,
    val roles: List<String>,
    val permissions: List<String>
)

@Serializable
data class Username(
    val id: Int,
    val username: String,
    val email: String,
    val imagen_url: String? = null
)

@Serializable
data class LoginInput(
    val name: String,
    val last_name: String,
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val status: Boolean,
    val message: String,
    val data: ResponseData
)

@Serializable
data class ResponseData(
    val token: String,
    val user: UserResponse,
    val roles: List<String>
)

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String
)

@Serializable
data class MenuItem(
    val id: String,
    val title: String?,
    val subtitle: String?,
    val type: String?,
    val icon: String? = null,
    val status: Boolean = false,  // Valor predeterminado
    val moduleOrder: Int? = null,
    val link: String,
    val parentModuleId: String? = null,
    val children: List<MenuItem>? = null
)


@Serializable
data class MenuResponse(
    val data: List<MenuItem>
)

fun decodeToken(token: String): User? {
    return try {
        // Dividir el token en sus 3 partes: header, payload, signature
        val parts = token.split(".")
        if (parts.size != 3) {
            println("Token inválido: No tiene 3 partes.")
            return null
        }
        // Decodificar la segunda parte (payload) del token desde base64
        val payloadJson = parts[1].decodeBase64String()

        // Parsear el payload como un objeto JSON
        val jsonObject = Json.decodeFromString<JsonObject>(payloadJson)

        // Verificación de valores esperados en el payload
        val id = jsonObject["sub"]?.jsonPrimitive?.content ?: "default_id"
        val username = jsonObject["username"]?.jsonPrimitive?.content ?: "Desconocido"
        val email = jsonObject["email"]?.jsonPrimitive?.content ?: "Sin email"

        // Imprimir el contenido del token para depuración
        println("Decoded Token: ID=$id, Username=$username, Email=$email")

        // Crear el objeto `User` con la información extraída
        User(
            id = id,  // Guardar el id
            name = username,  // Guardar el nombre
            email = email,  // Guardar el email
            token = token  // Guardar el token
        )
    } catch (e: Exception) {
        println("Error decodificando el token: ${e.message}")
        null
    }
}


fun formatDateTime(dateTime: String): String {
    return try {
        val parts = dateTime.split("T")
        val datePart = parts[0].split("-")
        val timePart = parts[1].substring(0, 5)

        "${datePart[2]}/${datePart[1]}/${datePart[0].takeLast(2)} $timePart"
    } catch (e: Exception) {
        dateTime
    }
}