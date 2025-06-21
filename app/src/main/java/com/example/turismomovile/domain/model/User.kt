package com.example.turismomovile.domain.model

import com.example.turismomovile.data.remote.dto.UserResponse

data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String,  // Agregamos token aquí
)

// Función para convertir UserResponse a User
fun UserResponse.toUser(token: String): User {  // Recibe el token como parámetro
    return User(
        id = this.id.toString(),  // Si el ID es entero, lo convertimos a String
        email = this.email,
        name = this.username,  // Usamos username como nombre
        token = token  // Usamos el token recibido como parámetro
    )
}
