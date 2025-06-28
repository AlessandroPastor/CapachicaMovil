package com.example.turismomovile.domain.repository.configuration

import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionResponse
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionUpdateDTO
import com.example.turismomovile.data.remote.dto.configuracion.AsociacionWithFamily

interface AsociacionesRepository {
    suspend fun getAsociaciones(page: Int = 0, size: Int = 3, name: String? = null): Result<AsociacionResponse>

    suspend fun getAsociacionesById(id: String): Result<Asociacion>

    suspend fun createAsociaciones(dto: AsociacionCreateDTO): Result<Asociacion>

    suspend fun updateAsociaciones(id: String, dto: AsociacionUpdateDTO): Result<Asociacion>

    suspend fun deleteAsociaciones(id: String): Result<Unit>

    suspend fun getAsociacionWithEmprendedor(id: String): Result<AsociacionWithFamily>

}