package com.example.turismomovile.domain.usecase

import com.example.turismomovile.domain.model.User
import com.example.turismomovile.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}