package com.example.turismomovile.domain.repository.configuration

import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociaciones
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesByAsoacionesResponse
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ImgAsociacionesResponse

interface ImgAsociacionesRepository {
    suspend fun getImgAsoaciones(page: Int = 0, size: Int = 10, name: String? = null): Result<ImgAsociacionesResponse>
    suspend fun getImgAsoacionesByAsoaciones(page: Int = 0, size: Int = 10, name: String? = null,asociacionId: String): Result<ImgAsociacionesByAsoacionesResponse>


    suspend fun getImgAsoacionesById(id: String): Result<ImgAsociaciones>

    suspend fun createImgAsoaciaciones(asoaciones: ImgAsociacionesCreateDTO): Result<ImgAsociaciones>

    suspend fun updateImgAsoaciones(id: String, emprendedor: ImgAsociaciones): Result<ImgAsociaciones>

    suspend fun deleteImgAsoaciones(id: String): Result<Unit>

}