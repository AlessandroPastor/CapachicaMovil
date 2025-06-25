package com.example.turismomovile.domain.model

import com.example.turismomovile.data.remote.dto.UserResponse
import com.example.turismomovile.data.remote.dto.decodeToken

data class User(
    val id: String,
    val email: String,
    val name: String,
    val lastName: String,
    val fullName: String?,
    val username: String,
    val code: String?,
    val imagenUrl: String?,
    val roles: List<String>,
    val permissions: List<String>,
    val createdAt: String?,
    val token: String,
)

// Función para convertir UserResponse a User
fun UserResponse.toUser(token: String): User? {
    val decoded = decodeToken(token)
    return decoded?.name?.let {
        User(
        id = this.id.toString(),
        email = decoded?.email ?: this.email,
        name = it,
        lastName = decoded?.lastName ?: "",
        fullName = decoded?.fullName,
        username = decoded?.username ?: this.username,
        code = decoded?.code,
        imagenUrl = decoded?.imagenUrl,
        roles = decoded?.roles ?: emptyList(),
        permissions = decoded?.permissions ?: emptyList(),
        createdAt = decoded?.createdAt,
        token = token
    )
    }
}

// Funciones de extensión para el modelo User
fun User.hasRole(role: String): Boolean {
    return roles.contains(role)
}

fun User.hasPermission(permission: String): Boolean {
    return permissions.contains(permission)
}

fun User.isAdmin(): Boolean {
    return hasRole("admin_familia")
}

fun User.isUser(): Boolean {
    return hasRole("usuario")
}

fun User.hasProfileImage(): Boolean {
    return !imagenUrl.isNullOrEmpty()
}