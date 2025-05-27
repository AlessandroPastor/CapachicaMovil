package com.example.turismomovile.data.repository.configuration

import com.example.turismomovile.data.remote.api.configuracion.ModuleApiService
import com.example.turismomovile.data.remote.dto.configuracion.ModuleCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ModuleDTO
import com.example.turismomovile.data.remote.dto.configuracion.ModuleResponse
import com.example.turismomovile.data.remote.dto.configuracion.ModuleSelectedDTO
import com.example.turismomovile.domain.repository.configuration.ModuleRepository

class ModuleRepositoryImpl(
    private val apiModuleService: ModuleApiService
) : ModuleRepository {

    override suspend fun getModules(page: Int, size: Int, name: String?): Result<ModuleResponse> {
        return try {
            Result.success(apiModuleService.getModules(page, size, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getModuleById(id: String): Result<ModuleDTO> {
        return try {
            val module = apiModuleService.getModuleById(id)
            Result.success(module)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createModule(module: ModuleCreateDTO): Result<ModuleDTO> {
        return try {
            Result.success(apiModuleService.createModule(module))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateModule(id: String, module: ModuleCreateDTO): Result<ModuleDTO> {
        return try {
            Result.success(apiModuleService.updateModule(id, module))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteModule(id: String): Result<Unit> {
        return try {
            Result.success(apiModuleService.deleteModule(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getModulesSelected(roleId: String, parentModuleId: String): Result<List<ModuleSelectedDTO>> {
        return try {
            Result.success(apiModuleService.getModulesSelected(roleId, parentModuleId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
