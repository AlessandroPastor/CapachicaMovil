package com.example.turismomovile.data.remote.dto

import com.example.turismomovile.domain.model.User
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import android.util.Base64

// LOGIN
@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
)
@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)

@Serializable
data class LoginData(
    val token: String,
    val expiresAt: String,
    val username: UserDatas,
    val roles: List<String>,
    val permissions: List<String>
)

@Serializable
data class UserDatas(
    val id: Int,
    val name: String,
    val lastName: String,
    val username: String,
    val email: String?
)
// FIN LOGIN


// REGISTER
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
    val status: Boolean? = null,
    val message: String,
    val data: ResponseData? = null   // allow absence of 'data'
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
// FIN REGISTER



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
        val payload = parts[1]
        // Agregar padding si es necesario para Base64
        val paddedPayload = when (payload.length % 4) {
            2 -> payload + "=="
            3 -> payload + "="
            else -> payload
        }

        val payloadBytes = Base64.decode(paddedPayload, Base64.URL_SAFE)
        val payloadJson = String(payloadBytes)

        // Parsear el payload como un objeto JSON
        val jsonObject = Json.decodeFromString<JsonObject>(payloadJson)

        // Extraer todos los campos del JWT según tu getJWTCustomClaims()
        val id = jsonObject["id"]?.jsonPrimitive?.content ?:
        jsonObject["sub"]?.jsonPrimitive?.content ?: "default_id"
        val name = jsonObject["name"]?.jsonPrimitive?.content ?: "Sin nombre"
        val lastName = jsonObject["last_name"]?.jsonPrimitive?.content ?: "Sin apellido"
        val fullName = jsonObject["full_name"]?.jsonPrimitive?.content ?: "$name $lastName"
        val username = jsonObject["username"]?.jsonPrimitive?.content ?: "Sin username"
        val email = jsonObject["email"]?.jsonPrimitive?.content ?: "Sin email"
        val code = jsonObject["code"]?.jsonPrimitive?.content
        val imagenUrl = jsonObject["imagen_url"]?.jsonPrimitive?.content
        val createdAt = jsonObject["created_at"]?.jsonPrimitive?.content

        // Extraer roles (array de strings)
        val roles = jsonObject["roles"]?.jsonArray?.map {
            it.jsonPrimitive.content
        } ?: emptyList()

        // Extraer permisos (array de strings)
        val permissions = jsonObject["permissions"]?.jsonArray?.map {
            it.jsonPrimitive.content
        } ?: emptyList()

        // Imprimir el contenido del token para depuración
        println("Decoded Token: ID=$id, Name=$name, LastName=$lastName, Username=$username, Email=$email")
        println("Roles: $roles")
        println("Permissions: $permissions")

        // Crear el objeto `User` con toda la información extraída
        User(
            id = id,
            email = email,
            name = name,
            lastName = lastName,
            fullName = fullName,
            username = username,
            code = code,
            imagenUrl = imagenUrl,
            roles = roles,
            permissions = permissions,
            createdAt = createdAt,
            token = token
        )
    } catch (e: Exception) {
        println("Error decodificando el token: ${e.message}")
        e.printStackTrace()
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