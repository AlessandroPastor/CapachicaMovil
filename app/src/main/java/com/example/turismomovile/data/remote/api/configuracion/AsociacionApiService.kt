    package com.example.turismomovile.data.remote.api.configuracion

    import com.example.turismomovile.data.remote.api.ApiConstants
    import com.example.turismomovile.data.remote.api.base.BaseApiService
    import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
    import com.example.turismomovile.data.remote.dto.configuracion.AsociacionCreateDTO
    import com.example.turismomovile.data.remote.dto.configuracion.AsociacionResponse
    import com.example.turismomovile.data.remote.dto.configuracion.AsociacionWithFamily
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

    class AsociacionApiService (client: HttpClient): BaseApiService(client) {

        suspend fun getAsociaciones(page: Int = 0, size: Int = 10, name: String?): AsociacionResponse {
            return client.get(ApiConstants.Configuration.ASOCIACION_GET) {
                parameter("page", page)
                parameter("size", size)
            }.body()
        }

        suspend fun getAsociacionByid(id: String): Asociacion {
            return client.get(ApiConstants.Configuration.ASOCIACION_GETBYID.replace("{id}", id)) {
                addAuthHeader()
            }.body()
        }

        suspend fun getAsociacionWithEmprendedor(id: String): AsociacionWithFamily {
            return client.get(ApiConstants.Configuration.ASOCIACION_WITH_FAMILY.replace("{id}", id)) {
                addAuthHeader()
            }.body()
        }


        suspend fun createAsociacion(module: AsociacionCreateDTO): Asociacion {
            return client.post(ApiConstants.Configuration.ASOCIACION_POST) {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(module)
            }.body()
        }

        suspend fun updateAsociacion(id: String, asociacion: Asociacion): Asociacion {
            return client.put(ApiConstants.Configuration.ASOCIACION_PUT.replace("{id}", id)) {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(asociacion)
            }.body()
        }


        suspend fun deleteAsociacion(id: String) {
            client.delete(ApiConstants.Configuration.ASOCIACION_DELETE.replace("{id}", id)) {
                addAuthHeader()
            }
        }
    }