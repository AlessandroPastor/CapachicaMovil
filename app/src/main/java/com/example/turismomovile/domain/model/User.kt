package com.example.turismomovile.domain.model

import com.example.turismomovile.data.remote.dto.UserResponse

data class User(
    val id: String,
    val email: String,
    val name: String?,
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
fun UserResponse.toUser(token: String): User {
    return User(
        id = this.id.toString(),
        email = this.email,
        name = this.name,
        lastName = this.lastName,
        fullName = this.fullName,
        username = this.username,
        code = this.code,
        imagenUrl = this.imagenUrl,
        roles = this.roles ?: emptyList(),
        permissions = this.permissions ?: emptyList(),
        createdAt = this.createdAt,
        token = token
    )
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

fun User.getInitials(): String {
    return "${name.firstOrNull()?.uppercaseChar() ?: ''}${lastName.firstOrNull()?.uppercaseChar() ?: ''}"
}

fun User.hasProfileImage(): Boolean {
    return !imagenUrl.isNullOrEmpty()
}