package com.example.turismomovile.data.remote.api.ventas

import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.BaseApiService
import com.example.turismomovile.data.remote.dto.ventas.SaleCreateDTO
import com.example.turismomovile.data.remote.dto.ventas.SaleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SaleApiService(
    client: HttpClient,
    sessionManager: SessionManager
) : BaseApiService(client, sessionManager) {

    suspend fun createSale(sale: SaleCreateDTO): SaleResponse {
        val response = client.post(ApiConstants.Configuration.SALE_POST) {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(sale)
        }
        return response.body()
    }
}