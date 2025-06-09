package com.example.turismomovile.domain.repository.configuration

import com.example.turismomovile.data.remote.dto.configuracion.Municipalidad
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadDescriptionResponse
import com.example.turismomovile.data.remote.dto.configuracion.MunicipalidadResponse

interface MunicipalidadRepository {
    suspend fun getMunicipalidad(page: Int = 0, size: Int = 10, name: String? = null): Result<MunicipalidadResponse>

    suspend fun getMunicipalidadDescription(page: Int = 0, size: Int = 10, name: String? = null): Result<MunicipalidadDescriptionResponse>

    suspend fun getMunicipalidadById(id: String): Result<Municipalidad>

    suspend fun createMunicipalidad(municipalidad: MunicipalidadCreateDTO): Result<Municipalidad>

    suspend fun updateMunicipalidad(id: String, municipalidad: Municipalidad): Result<Municipalidad>

    suspend fun deleteMunicipalidad(id: String): Result<Unit>
}