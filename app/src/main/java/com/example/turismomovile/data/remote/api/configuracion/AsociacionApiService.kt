    package com.example.turismomovile.data.remote.api.configuracion

    import com.example.turismomovile.data.local.SessionManager
    import com.example.turismomovile.data.remote.api.ApiConstants
    import com.example.turismomovile.data.remote.api.base.BaseApiService
    import com.example.turismomovile.data.remote.dto.configuracion.Asociacion
    import com.example.turismomovile.data.remote.dto.configuracion.AsociacionCreateDTO
    import com.example.turismomovile.data.remote.dto.configuracion.AsociacionResponse
    import com.example.turismomovile.data.remote.dto.configuracion.AsociacionUpdateDTO
    import io.ktor.client.HttpClient
    import io.ktor.client.call.body
    import io.ktor.client.request.delete
    import io.ktor.client.request.get
    import io.ktor.client.request.parameter
    import io.ktor.client.request.post
    import io.ktor.client.request.put
    import io.ktor.client.request.setBody
    import io.ktor.client.statement.bodyAsText
    import io.ktor.http.ContentType
    import io.ktor.http.contentType

    class AsociacionApiService (client: HttpClient,
                                sessionManager: SessionManager
    ): BaseApiService(client, sessionManager) {

        suspend fun getAsociaciones(page: Int = 0, size: Int = 3, name: String?): AsociacionResponse {
            return client.get(ApiConstants.Configuration.ASOCIACION_GET) {
                parameter("page", page)
                parameter("size", size)
                name?.let { parameter("name", it) }
            }.body()
        }

        suspend fun getAsociacionByid(id: String): Asociacion {
            return client.get(ApiConstants.Configuration.ASOCIACION_GETBYID.replace("{id}", id)) {
                addAuthHeader()
            }.body()
        }


        suspend fun createAsociacion(dto: AsociacionCreateDTO): Asociacion {
            println("🔄 [CREATE] Iniciando creación de Asociación...")
            println("📤 Enviando DTO para crear: $dto")
            val response = client.post(ApiConstants.Configuration.ASOCIACION_POST) {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            println("⬅️ [RESPONSE] Código: ${response.status}")
            println("⬅️ [RESPONSE] Body: ${response.bodyAsText()}") // <-- Verás el JSON crudo que devuelve tu backend

            val asociacion = response.body<Asociacion>()
            println("✅ Asociación creada: $asociacion")
            return asociacion
        }

        suspend fun updateAsociacion(id: String, dto: AsociacionUpdateDTO): Asociacion {
            println("🔄 [UPDATE] Iniciando actualización de Asociación...")
            println("📤 Enviando ID: $id")
            println("📤 Enviando DTO para actualización: $dto")

            val response = client.put(ApiConstants.Configuration.ASOCIACION_PUT.replace("{id}", id)) {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(dto)
            }

            println("⬅️ [RESPONSE] Código: ${response.status}")
            val rawBody = response.bodyAsText()
            println("⬅️ [RESPONSE] Body: $rawBody")

            val asociacion = response.body<Asociacion>()
            println("✅ Asociación actualizada: $asociacion")
            return asociacion
        }

        suspend fun deleteAsociacion(id: String) {
            client.delete(ApiConstants.Configuration.ASOCIACION_DELETE.replace("{id}", id)) {
                addAuthHeader()
            }
        }
    }