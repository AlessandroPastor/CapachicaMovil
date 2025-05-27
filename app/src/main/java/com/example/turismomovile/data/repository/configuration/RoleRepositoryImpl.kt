package com.example.turismomovile.data.repository.configuration

import com.example.turismomovile.data.remote.api.configuracion.RoleApiService
import com.example.turismomovile.data.remote.dto.configuracion.Role
import com.example.turismomovile.data.remote.dto.configuracion.RoleModulesRequest
import com.example.turismomovile.data.remote.dto.configuracion.RoleResponse
import com.example.turismomovile.domain.repository.configuration.RoleRepository

class RoleRepositoryImpl(
    private val apiService: RoleApiService
) : RoleRepository {

    override suspend fun getRoles(page: Int, size: Int, name: String?): Result<RoleResponse> {
        return try {
            Result.success(apiService.getRoles(page, size, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoleById(id: String): Result<Role> {
        return try {
            Result.success(apiService.getRoleById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRole(role: Role): Result<Role> {
        return try {
            Result.success(apiService.createRole(role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRole(role: Role): Result<Role> {
        return try {
            Result.success(apiService.updateRole(role.id.toString(), role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRole(id: String): Result<Unit> {
        return try {
            Result.success(apiService.deleteRole(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRoleModules(request: RoleModulesRequest): Result<Boolean> {
        return try {
            Result.success(apiService.updateRoleModules(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}