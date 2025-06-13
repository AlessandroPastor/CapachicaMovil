package com.example.turismomovile.domain.repository.configuration

import com.example.turismomovile.data.remote.dto.configuracion.Emprendedor
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorResponse

interface EmprendedorRepository {
    suspend fun getEmprendedores(page: Int = 0, size: Int = 20, name: String? = null): Result<EmprendedorResponse>

    suspend fun getEmprendedoresById(id: String): Result<Emprendedor>

    suspend fun createEmprendedores(asoaciones: EmprendedorCreateDTO): Result<Emprendedor>

    suspend fun updateEmprendedores(id: String, emprendedor: Emprendedor): Result<Emprendedor>

    suspend fun deleteEmprendedores(id: String): Result<Unit>

}