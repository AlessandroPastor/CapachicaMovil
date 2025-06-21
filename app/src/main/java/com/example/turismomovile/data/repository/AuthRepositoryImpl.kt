package com.example.turismomovile.data.repository

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.base.AuthApiService
import com.example.turismomovile.data.remote.api.base.MenuApiService
import com.example.turismomovile.domain.model.User
import com.example.turismomovile.domain.repository.AuthRepository
import com.example.turismomovile.data.remote.dto.LoginDTO
import com.example.turismomovile.data.remote.dto.MenuItem
import com.example.turismomovile.data.remote.dto.decodeToken

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val menuApiService: MenuApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val loginDTO = LoginDTO(username = email, password = password)
            val loginResponse = authApiService.login(loginDTO)

            if (loginResponse.data.token.isNotEmpty()) {
                authApiService.updateAuthToken(loginResponse.data.token)

                val user = decodeToken(loginResponse.data.token)
                    ?: return Result.failure(Exception("No se pudo extraer el usuario del token"))

                sessionManager.saveUser(user)

                Result.success(user)
            } else {
                Result.failure(Exception("Error de autenticación: Token vacío"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error durante el login: ${e.message}"))
        }
    }

    override suspend fun getUserDetails(): Result<User> {
        return try {
            val user = sessionManager.getUser()
            if (user != null) {
                authApiService.updateAuthToken(user.token) // Reasignar token si necesario
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado en sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMenuItems(): Result<List<MenuItem>> {
        return try {
            Result.success(menuApiService.getMenuItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun logout() {
        sessionManager.clearSession()
        authApiService.clearAuthToken()
        menuApiService.clearAuthToken()
    }
    override suspend fun loadAuthToken() {
        authApiService.loadAuthTokenFromStorage()
    }



}
