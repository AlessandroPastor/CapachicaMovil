package com.example.turismomovile.domain.repository.configuration

import com.example.turismomovile.data.remote.dto.configuracion.ParentModule
import com.example.turismomovile.data.remote.dto.configuracion.ParentModuleDetail
import com.example.turismomovile.data.remote.dto.configuracion.ParentModuleListResponse

interface ParentModuleRepository {

    suspend fun getParentModules(page: Int = 0, size: Int = 20, name: String? = null): Result<ParentModuleListResponse>

    suspend fun getParentModuleById(id: String): Result<ParentModule>

    suspend fun createParentModule(parentModule: ParentModule): Result<ParentModule>

    suspend fun updateParentModule(id: String, parentModule: ParentModule): Result<ParentModule>

    suspend fun deleteParentModule(id: String): Result<Unit>

    suspend fun getParentModuleList(): Result<List<ParentModule>>

    suspend fun getParentModuleDetailList(): Result<List<ParentModuleDetail>>


}