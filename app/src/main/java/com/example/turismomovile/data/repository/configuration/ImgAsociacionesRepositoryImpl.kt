package io.dev.kmpventas.data.repository.configuration

import com.example.turismomovile.data.remote.api.configuracion.ImgAsociacionesApiService
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociaciones
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesByAsoacionesResponse
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesResponse
import com.example.turismomovile.domain.repository.configuration.ImgAsociacionesRepository

class ImgAsociacionesRepositoryImpl(
    private val apiService: ImgAsociacionesApiService
) : ImgAsociacionesRepository {

    // Obtener lista de asociaciones
    override suspend fun getImgAsoaciones(page: Int, size: Int, name: String?): Result<ImgAsociacionesResponse> {
        return try {
            Result.success(apiService.getImgAsoaciones()) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getImgAsoacionesByAsoaciones(page: Int, size: Int, name: String?,asociacionId: String): Result<ImgAsociacionesByAsoacionesResponse> {
        return try {
            Result.success(apiService.getImgAsoacionesByAsoaciones(asociacionId)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener una asociación por ID
    override suspend fun getImgAsoacionesById(id: String): Result<ImgAsociaciones> {
        return try {
            Result.success(apiService.getImgAsoacionesById(id)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crear una nueva asociación
    override suspend fun createImgAsoaciaciones(asociacion: ImgAsociacionesCreateDTO): Result<ImgAsociaciones> {
        return try {
            Result.success(apiService.createImgAsoaciones(asociacion)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar una asociación
    override suspend fun updateImgAsoaciones(id: String, asociacion: ImgAsociaciones): Result<ImgAsociaciones> {
        return try {
            Result.success(apiService.updateImgAsoaciones(id, asociacion)) // Llamamos al servicio correspondiente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar una asociación
    override suspend fun deleteImgAsoaciones(id: String): Result<Unit> {
        return try {
            apiService.deleteImgAsocioanes(id) // Llamamos al servicio correspondiente
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}