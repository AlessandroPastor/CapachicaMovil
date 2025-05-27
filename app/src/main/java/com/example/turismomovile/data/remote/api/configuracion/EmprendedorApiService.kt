    package com.example.turismomovile.data.remote.api.configuracion

    import com.example.turismomovile.data.remote.api.ApiConstants
    import com.example.turismomovile.data.remote.api.base.BaseApiService
    import com.example.turismomovile.data.remote.dto.configuracion.Emprededor
    import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorCreateDTO
    import com.example.turismomovile.data.remote.dto.configuracion.EmprendedorResponse
    import io.ktor.client.HttpClient
    import io.ktor.client.call.body
    import io.ktor.client.request.delete
    import io.ktor.client.request.get
    import io.ktor.client.request.parameter
    import io.ktor.client.request.post
    import io.ktor.client.request.put
    import io.ktor.client.request.setBody
    import io.ktor.http.ContentType
    import io.ktor.http.contentType

    class EmprendedorApiService (client: HttpClient): BaseApiService(client) {

        suspend fun getEmprendedor(page: Int = 0, size: Int = 10, name: String?): EmprendedorResponse {
            return client.get(ApiConstants.Configuration.EMPRENDEDORES_GET) {
                parameter("page", page)
                parameter("size", size)
                addAuthHeader()
            }.body()
        }

        suspend fun getEmprendedorByid(id: String): Emprededor {
            return client.get(ApiConstants.Configuration.EMPRENDEDORES_GETBYID.replace("{id}", id)) {
                addAuthHeader()
            }.body()
        }

        suspend fun createEmprendedor(module: EmprendedorCreateDTO): Emprededor {
            return client.post(ApiConstants.Configuration.EMPRENDEDORES_POST) {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(module)
            }.body()
        }

        suspend fun updateEmprendedor(id: String, asociacion: Emprededor): Emprededor {
            return client.put(ApiConstants.Configuration.EMPRENDEDORES_PUT.replace("{id}", id)) {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(asociacion)
            }.body()
        }

        suspend fun deleteEmprendedor(id: String) {
            client.delete(ApiConstants.Configuration.EMPRENDEDORES_DELETE.replace("{id}", id)) {
                addAuthHeader()
            }
        }
    }