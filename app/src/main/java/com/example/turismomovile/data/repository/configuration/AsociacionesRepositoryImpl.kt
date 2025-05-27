package com.example.turismomovile.data.repository.configuration

import com.example.turismomovile.data.remote.api.configuracion.AsociacionApiService
import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionResponse
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionWithFamily
import com.example.turismomovile.domain.repository.configuration.AsociacionesRepository

class AsociacionesRepositoryImpl(
    private val apiService: AsociacionApiService
) : AsociacionesRepository {

    // Obtener lista de asociaciones
    override suspend fun getAsociaciones(page: Int, size: Int, name: String?): Result<AsociacionResponse> {
        return try {
            Result.success(apiService.getAsociaciones(page, size, name)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener una asociación por ID
    override suspend fun getAsociacionesById(id: String): Result<Asociacion> {
        return try {
            Result.success(apiService.getAsociacionByid(id)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crear una nueva asociación
    override suspend fun createAsociaciones(asoaciones: AsociacionCreateDTO): Result<Asociacion> {
        return try {
            Result.success(apiService.createAsociacion(asoaciones)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar una asociación
    override suspend fun updateAsociaciones(id: String, asoaciones: Asociacion): Result<Asociacion> {
        return try {
            Result.success(apiService.updateAsociacion(id, asoaciones)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar una asociación
    override suspend fun deleteAsociaciones(id: String): Result<Unit> {
        return try {
            apiService.deleteAsociacion(id) // Llamamos al servicio correspondiente
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener asociación con emprendedores/familias
    override suspend fun getAsociacionWithEmprendedor(id: String): Result<AsociacionWithFamily> {
        return try {
            Result.success(apiService.getAsociacionWithEmprendedor(id)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}