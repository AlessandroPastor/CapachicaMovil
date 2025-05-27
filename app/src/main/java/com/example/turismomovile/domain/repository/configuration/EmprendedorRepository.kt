package com.example.turismomovile.domain.repository.configuration

import com.example.turismomovile.data.remote.dto.configuracion.Emprededor
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorResponse

interface EmprendedorRepository {
    suspend fun getEmprendedores(page: Int = 0, size: Int = 20, name: String? = null): Result<EmprendedorResponse>

    suspend fun getEmprendedoresById(id: String): Result<Emprededor>

    suspend fun createEmprendedores(asoaciones: EmprendedorCreateDTO): Result<Emprededor>

    suspend fun updateEmprendedores(id: String, emprendedor: Emprededor): Result<Emprededor>

    suspend fun deleteEmprendedores(id: String): Result<Unit>

}