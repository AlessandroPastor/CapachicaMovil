package com.example.turismomovile.data.repository.configuration

import com.example.turismomovile.data.remote.api.configuracion.MunicipalidadApiService
import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionResponse
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadResponse
import com.example.turismomovile.domain.repository.configuration.MunicipalidadRepository

class MunicipalidadRepositoryImpl (
    private val apiService: MunicipalidadApiService
): MunicipalidadRepository {


    override suspend fun getMunicipalidad(page: Int, size: Int, name: String?): Result<MunicipalidadResponse> {
        return try {
            Result.success(apiService.getMunicipalidad(page, size, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMunicipalidadDescription(page: Int, size: Int, name: String?): Result<MunicipalidadDescriptionResponse> {
        return try {
            Result.success(apiService.getMunicipalidadDescription(page, size, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getMunicipalidadById(id: String): Result<Municipalidad> {
        return try {
            Result.success(apiService.getMunicipalidadById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createMunicipalidad(municipalidad: MunicipalidadCreateDTO): Result<Municipalidad> {
        return try {
            Result.success(apiService.createMunicipalidad(municipalidad))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    override suspend fun updateMunicipalidad(id: String, municipalidad: Municipalidad): Result<Municipalidad> {
        return try {
            Result.success(apiService.updateMunicipalidad(id, municipalidad))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMunicipalidad(id: String): Result<Unit> {
        return try {
            apiService.deleteMunicipalidad(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}